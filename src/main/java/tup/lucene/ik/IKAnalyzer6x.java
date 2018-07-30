package tup.lucene.ik;

import lombok.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class IKAnalyzer6x extends Analyzer {


    private boolean useSmart;

    private boolean useSmart(){
        return useSmart;
    }

    public IKAnalyzer6x() {
        this(false);
    }

    public IKAnalyzer6x(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer _IKTokenizer = new IKTokenizer6x(useSmart());
        return new TokenStreamComponents(_IKTokenizer);
    }
}
