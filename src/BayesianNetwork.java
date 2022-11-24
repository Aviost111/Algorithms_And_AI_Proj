import java.util.Hashtable;

public class BayesianNetwork {
    private Hashtable<String,BayesianNode> BN;


    public BayesianNetwork(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }

    public Hashtable<String, BayesianNode> getBN() {
        return BN;
    }

    public void setBN(Hashtable<String, BayesianNode> BN) {
        this.BN = BN;
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "BN=" + BN +
                '}';
    }
}
