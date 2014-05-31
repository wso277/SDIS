package communication;

import java.io.Serializable;

/**
 * Created by João on 31/05/2014.
 */
public class Network implements Serializable{
    private Address mc;
    private Address mcb;
    private Address mcr;

    public Network(Address mc,Address mcb,Address mcr) {
        this.mc = mc;
        this.mcr = mcr;
        this.mcb = mcb;
    }

    public Address getMc() {
        return mc;
    }

    public void setMc(Address mc) {
        this.mc = mc;
    }

    public Address getMcb() {
        return mcb;
    }

    public void setMcb(Address mcb) {
        this.mcb = mcb;
    }

    public Address getMcr() {
        return mcr;
    }

    public void setMcr(Address mcr) {
        this.mcr = mcr;
    }

}
