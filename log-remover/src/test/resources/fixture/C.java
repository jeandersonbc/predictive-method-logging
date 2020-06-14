package bla;

import org.apache.log4j.Logger;

class C {

    private static Logger log = Logger.getLogger(CK.class);

    public void m1() {
        int a = a + 1;
        int b = 10;

        if(a>1)
            log.info("b");

        if(b>1)
            log.info("c");
        else
            log.info("d");
    }
}
