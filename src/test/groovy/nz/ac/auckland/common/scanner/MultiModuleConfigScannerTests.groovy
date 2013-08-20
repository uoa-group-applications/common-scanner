package nz.ac.auckland.common.scanner

import groovy.transform.CompileStatic
import org.junit.Test

@CompileStatic
class MultiModuleConfigScannerTests {
  @Test
  public void basic() {
    // we should get 1 main, 1 test and a few jars.
    int jars = 0, main = 0, other = 0;

    assert new File("src/test/resources").exists()

    String userHome = System.getProperty("user.home");
    System.setProperty("user.home", new File(".").getAbsolutePath());
    System.setProperty(MultiModuleConfigScanner.SCANNER_HOME, "src/test/resources/webhome")

    try {
      MultiModuleConfigScanner.scan(new MultiModuleConfigScanner.Notifier() {
        @Override
        void underlayWar(URL url) {
          File f = new File(url.getPath())
          assert f.exists()
          other++
        }

        @Override
        void jar(URL url) {
//          println url.path
          File f = new File(url.toURI().path)
          assert f.exists()
          jars++
        }

        @Override
        void dir(URL url) {
//          println url.path
          File f = new File(url.toURI().path)
          assert f.exists()
          main++
        }

      })
    }
    finally {
      System.setProperty("user.home", userHome)
    }

    assert MultiModuleConfigScanner.appName() == "common-scanner"
    assert MultiModuleConfigScanner.appPath().endsWith("common-scanner/src/test/resources") ||
           MultiModuleConfigScanner.appPath().endsWith("common-scanner/target/checkout/src/test/resources")

    assert System.getProperty("cooties.value") == "5" // this one
    assert System.getProperty("onelostsheep.ofmine") == "hello" // common-scanner
    assert System.getProperty("should.override.more.properties") == "1" // in more.properties AND webhome/common-scanner/war.properties (2nd should override 1st)

    assert System.getProperty("only.in.this.file") == "wibble" // comes from more.properties

    assert MultiModuleConfigScanner.classpathGavs.size() != 0

    // only works in IDE
//    assert jars > 0
//    assert main == 2
//    assert other == 0
  }

  @Test
  public void parseGavTest() {
    File here = new File(".")

    MultiModuleConfigScanner.parseGAVfromPOM(here)

    MultiModuleConfigScanner.GroupArtifactVersion gav = MultiModuleConfigScanner.classpathGavs.get(here.absolutePath)
    assert gav != null
    assert gav.groupId == "nz.ac.auckland.common"
    assert gav.artfiactId == "common-scanner"
    assert gav.version != null

  }

}
