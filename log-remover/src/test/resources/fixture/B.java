package bla;

import org.apache.log4j.Logger;

class B {

    private Logger log = Logger.getLogger(CK.class);

    public void m1() {
        int a = a + 1;
        System.out.println("hi");

        // Some silly comment to break your log removal :)
        log.info(// Mom, look! I'm a creative devs
                "bla");
    }

    public void m2() {
        int a = a + 1;
        System.out.println("hi");
        if (a > 2) {
        }
    }

    public void m3() {
        int b = 1;
        int c = 2;
    }
}
