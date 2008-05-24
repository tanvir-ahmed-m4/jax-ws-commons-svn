// generate wrapper code from VirtualBox IDL
import com.sun.codemodel.*
import com.sun.xml.bind.api.impl.NameConverter
import javax.xml.ws.WebServiceException
import javax.xml.ws.Holder

// paramters for the code generation


// code starts here
def baseDir = project.basedir;

def xidl = new XmlParser().parse(new File(baseDir,"src/VirtualBox.xidl"));
this.library = xidl.library;

this.codeModel = new JCodeModel();
this.pkg = codeModel._package("com.sun.xml.ws.commons.virtualbox");
// VboxPortType generated by JAX-WS
this.portType = pkgRef("VboxPortType");



library["interface"].each { onInterface(it); }

File destDir = new File(baseDir, "target/generated-sources/wrappers")
destDir.mkdirs();
codeModel.build(destDir);
project.addCompileSourceRoot(destDir.toString());

/**
 * Generates the interface definition.
 *
 * @param intf
 *      XIDL "interface" element.
 */
def onInterface(intf) {
    if(intf.@wsmap=="suppress")
        return; // no mapping

    def interfaceName = intf.@name
    JDefinedClass clz = pkg._class(interfaceName);
    buildJavadoc(clz,intf)

    // the port type that we use for the communication
    port = clz.field(JMod.PUBLIC|JMod.FINAL,portType,"port");

    // the token passed through web service as the 'this' pointer
    _this = clz.field(JMod.PUBLIC|JMod.FINAL,String,"_this");

    // constructor
    JMethod cons = clz.constructor(JMod.PUBLIC);
    cons.param(String.class,"_this");
    cons.param(portType,"port");
    cons.body().directStatement("""
        this._this = _this;
        this.port = port;
    """)

    intf.attribute.each { attr ->
        def attrName = attr.@name

        // attribute getter method
        def type = outerType(attr.@type)

        JMethod get = clz.method(JMod.PUBLIC, type, "get" + cap(attrName));
        buildJavadoc(get,attr)
        def body = createTryCatchBlock(get.body());
        def retVal = body.decl(jaxwsType(attr.@type),"retVal",port.invoke(decap(interfaceName)+"Get"+cap(attrName)).arg(_this));
        body._return(unmarshal(attr.@type,retVal));

        if(!attr.@readonly) {
            // attribuet setter method
            JMethod set = clz.method(JMod.PUBLIC, void.class, "set" + cap(attrName));
            def value = marshal(attr.@type,set.param(outerType(attr.@type),"value"));
            buildJavadoc(set,attr)
            createTryCatchBlock(set.body()).invoke(port,decap(interfaceName)+"Set"+cap(attrName)).arg(_this).arg(value);
        }
    }

    // operation
    intf.method.each { method ->
        if(isMethodMappingSuppressed(method))  return;

        def methodName = method.@name

        def retParam = method.param.find { it.@dir=="return" }
        def retType = retParam == null ? void.class : outerType(retParam.@type)

        JMethod m = clz.method(JMod.PUBLIC, retType, methodName);
        buildJavadoc(m,method);
        if(retParam!=null)
            m.javadoc().addReturn().add(retParam.desc?.text())

        def body = createTryCatchBlock(m.body())

        String portMethodName = decap(interfaceName)+cap(methodName);
        JInvocation call;
        if(retParam==null) {
            call = body.invoke(port,portMethodName)
        } else {
            call = port.invoke(portMethodName);
            body._return(unmarshal(retParam.@type,body.decl(jaxwsType(retParam.@type),"retVal",call)));
        }
        // implicit first arg
        call.arg("_this");

        method.param.each { param ->
            switch(param.@dir) {
            case "return":  return;
            case "out":
                JVar p = m.param(codeModel.ref(Holder.class).narrow(outerType(param.@type).boxify()), param.@name);
                m.javadoc().addParam(param.@name).add(param.desc?.text())

                call.arg(p); // TODO: marshalling?
                break;

            case "in":
                JVar p = m.param(outerType(param.@type), param.@name);
                m.javadoc().addParam(param.@name).add(param.desc?.text())

                call.arg(marshal(param.@type,p));
                break;
            }
        }
    }
}

private boolean isMethodMappingSuppressed(method) {
    return method.param.find { p -> library."interface".find { it.@name==p.@type && it.@wsmap=="suppress" } };
}

private JBlock createTryCatchBlock(JBlock block) {
    JTryBlock tblock = block._try();
    ["InvalidObjectFaultMsg", "RuntimeFaultMsg"].each {
        JCatchBlock cb = tblock._catch(pkgRef(it))
        cb.body()._throw(JExpr._new(codeModel.ref(WebServiceException)).arg(cb.param("e")));
    }

    return tblock.body();
}

/**
 * Generates the code that takes the return value from web service and return in-memory type.
 */
def unmarshal(String typeName, JVar expr) {
    if(typeName.startsWith("I")) {
        if(typeName.endsWith("Collection")) {
            // collection
            return codeModel.ref("Helper").staticInvoke("wrap")
                .arg(codeModel.ref(getComponentName(typeName)).dotclass())
                .arg(JExpr.ref("port"))
                .arg(JOp.cond(expr.eq(JExpr._null()), JExpr._null(), expr.invoke("getArray")));
        }

        // interface wrapper
        return JExpr._new(codeModel.ref(typeName)).arg(expr).arg(JExpr.ref("port"));
    }
    if(typeName=="uuid") {
        return codeModel.ref(UUID).staticInvoke("fromString").arg(expr);
    }
    return expr;
}

def marshal(String typeName, JVar expr) {
    if(typeName=="uuid") {
        return expr.invoke("toString");
    }
    if(typeName.startsWith("I"))
        return JOp.cond(expr.eq(JExpr._null()), JExpr._null(), expr.ref("_this"));
    return expr;
}

private String getComponentName(String typeName) {
    return typeName.substring(0, typeName.length() - "Collection".length())
}

/**
 * Builds javadoc from the nested <desc> tag in XML and attaches to the source tree.
 */
def buildJavadoc(documentable,xml) {
    if(xml.desc==null)  return;

    documentable.javadoc().add(xml.desc.text())
}

/**
 * Returns the Java type used in the publicly visible part of the API that represents the given type name in XIDL.
 */
JType outerType(String typeName) {
    if(typeName==null)  return codeModel.VOID;
    if(typeName =~ /I.+Collection/) {
        return codeModel.ref(List).narrow(pkgRef(getComponentName(typeName)));
    }
    
    switch(typeName) {
    case "wstring":             return codeModel.ref(String);
    case "uuid":                return codeModel.ref(UUID);
    case "unsigned long long":
    case "unsigned long":       return codeModel.LONG;
    case "unsigned short":      return codeModel.INT;
    }
    return codeModel.ref(typeName);
}

/**
 * Returns the Java type JAX-WS uses for the given type name in XIDL.
 */
JType jaxwsType(String typeName) {
    if(typeName =~ /I.+Collection/) {
        return pkgRef("ArrayOf"+getComponentName(typeName));
    }
    if(typeName.startsWith("I")) {
        return codeModel.ref(String);
    }
    return outerType(typeName);
}

String cap(String str) {
    return ""+Character.toUpperCase(str.charAt(0))+str.substring(1);
}
String decap(String str) {
    return NameConverter.standard.toVariableName(str);
}
JType pkgRef(String typeName) {
    return codeModel.ref(pkg.name()+"."+typeName);
}