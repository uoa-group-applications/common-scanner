common-scanner
==============

This introduces a single class, MultiModuleConfigScanner - which looks  through the URLClassPath and fires events based on the files it finds.

    public interface Notifier {
      public void underlayWar(URL url) throws Exception;

      public void jar(URL url) throws Exception;

      public void dir(URL url) throws Exception;
    }

Calling MultiModuleConfigScanner.scan - you provide an implementation of the notifier. It tells you when it finds

+ files ending in -underlay.war get passed to underlayWar()
+ files ending in .jar get passed to jar()
+ directories ending in X/target/classes get searched for X/src/main/resources and X/src/main/webapp. directories ending in X/target/test-classes gets examined for X/src/test/resources and X/src/test/webapp

Various applications (e.g. JAWR, Runnable War) use the scanner.

Configuration
-------------
The scanner also looks for configuration for the current project and injects it into the system properties if it finds a target/test-classes on the classpath.
This is because we know we are running tests and are in the IDE or running Maven from the command line. It will for a Maven module called /fat-domain/target/test-classes
determine that the Maven module is called fat-domain. We want to share properties across all of the Maven artifacts for fat (fat-admin, fat-albert, etc),
it will then attemp to load:

    /fat-domain/src/test/war.properties
    ~/.webdev/fat-domain/war.properties

~ refers to the System.getProperty("user.home") value.

If there is a line in either (or both) of these files called

    scanner.commonConfigFiles=$home/fat.properties, /etc/fat/fat.properties

This will cause it to load like this (if this line is in /fat-domain/src/test/war.properties):

    /fat-domain/src/test/war.properties      # module specific fat-domain ones that are common to everyone checking out the artifact
    ~/.webdev/fat.properties                 # fat common ones
    /etc/fat/fat.properties                  # fat common ones
    ~/.webdev/fat-domain/war.properties      # module specific overriding all other properties (including "fat" common ones)


$home is replaced with the line:

     ~/.webdev

Unless the user specifies the system property "scanner.home" or an environment variable WAR_SCANNER_HOME

Overriding these is useful if you wish to store your configuration in (say) Dropbox or Google Drive or point it elsewhere for a test.

