Steps to run "mvn test":

1. Create an AWS X.509 certificate.
2. Store the X.509 certificate under <user.home>/.ec2/cert.pem and the private key under <user.home>/.ec2/pk.pem.
3. Run "mvn test".


Releases older than Metro 1.4 are likely to fail with this error message:

 SEVERE: WSSTUBE0025: Error in Verifying Security in the Inbound Message.
 com.sun.xml.wss.XWSSecurityException: Security Requirements not met - No Security header in message
         at com.sun.xml.ws.security.opt.impl.incoming.SecurityRecipient.createMessage(SecurityRecipient.java:880)
         at com.sun.xml.ws.security.opt.impl.incoming.SecurityRecipient.validateMessage(SecurityRecipient.java:229)
         at com.sun.xml.wss.jaxws.impl.SecurityTubeBase.verifyInboundMessage(SecurityTubeBase.java:438)
         at com.sun.xml.wss.jaxws.impl.SecurityClientTube.processClientResponsePacket(SecurityClientTube.java:339)
         at com.sun.xml.wss.jaxws.impl.SecurityClientTube.processResponse(SecurityClientTube.java:272)
         at com.sun.xml.ws.api.pipe.Fiber.__doRun(Fiber.java:608)
         at com.sun.xml.ws.api.pipe.Fiber._doRun(Fiber.java:557)
         at com.sun.xml.ws.api.pipe.Fiber.doRun(Fiber.java:542)
         at com.sun.xml.ws.api.pipe.Fiber.runSync(Fiber.java:439)
         at com.sun.xml.ws.client.Stub.process(Stub.java:222)
         at com.sun.xml.ws.client.sei.SEIStub.doProcess(SEIStub.java:135)
         at com.sun.xml.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:109)
         at com.sun.xml.ws.client.sei.SyncMethodHandler.invoke(SyncMethodHandler.java:89)
         at com.sun.xml.ws.client.sei.SEIStub.invoke(SEIStub.java:118)
         at $Proxy44.describeImages(Unknown Source)
         at FooTest.test1(FooTest.java:36)
         at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
         at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
         at java.lang.reflect.Method.invoke(Method.java:585)
         at junit.framework.TestCase.runTest(TestCase.java:154)
         at junit.framework.TestCase.runBare(TestCase.java:127)
         at junit.framework.TestResult$1.protect(TestResult.java:106)
         at junit.framework.TestResult.runProtected(TestResult.java:124)
         at junit.framework.TestResult.run(TestResult.java:109)
         at junit.framework.TestCase.run(TestCase.java:118)
         at junit.framework.TestSuite.runTest(TestSuite.java:208)
         at junit.framework.TestSuite.run(TestSuite.java:203)
         at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
         at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
         at java.lang.reflect.Method.invoke(Method.java:585)
         at org.apache.maven.surefire.junit.JUnitTestSet.execute(JUnitTestSet.java:213)
         at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.executeTestSet(AbstractDirectoryTestSuite.java:140)
         at org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.execute(AbstractDirectoryTestSuite.java:127)
         at org.apache.maven.surefire.Surefire.run(Surefire.java:177)
         at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
         at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
         at java.lang.reflect.Method.invoke(Method.java:585)
         at org.apache.maven.surefire.booter.SurefireBooter.runSuitesInProcess(SurefireBooter.java:338)
         at org.apache.maven.surefire.booter.SurefireBooter.main(SurefireBooter.java:997)
 Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 3.69 sec <<< FAILURE!
