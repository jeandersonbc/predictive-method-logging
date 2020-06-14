package bla;

import org.apache.log4j.Logger;

class A {

    private static Logger log = Logger.getLogger(CK.class);

    public void m1() {
        int a = a + 1;
        System.out.println("hi");
    }

    public void m2() {
        int a = a + 1;
        System.out.println("hi");
        if (a > 2) {
            log.info("a");
        }
    }

    public void m3() {
        int b = 1;
        int c = 2;

        log.error("b");
    }

    public void m4() {
        int a = 0;

        try {

        } catch(Exception e) {
            log.info("msg " + a, e);
        }
    }
}
