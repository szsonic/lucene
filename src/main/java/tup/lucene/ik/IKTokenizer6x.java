package tup.lucene.ik;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

public class IKTokenizer6x extends Tokenizer {
    /**
     * IK分析器实现
     */
    private IKSegmenter _IKImplement;

    /**
     * 词元文本属性
     */
    private final CharTermAttribute termAtt;

    /**
     * 词元位移属性
     */
    private final OffsetAttribute offsetAtt;

    /**
     * 词元分类属性
     */
    private final TypeAttribute typeAtt;

    /**
     * 记录最后一次词元的位置
     */
    private int endPosition;

    public IKTokenizer6x(boolean useSmart) {
        super();
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.offsetAtt = addAttribute(OffsetAttribute.class);
        this.typeAtt = addAttribute(TypeAttribute.class);
        _IKImplement = new IKSegmenter(input,useSmart);
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        Lexeme nextLexeme = _IKImplement.next();
        if (nextLexeme != null) {
            //将Lexeme转成Attributes
            termAtt.append(nextLexeme.getLexemeText()); //设置词元文本
            termAtt.setLength(nextLexeme.getLength()); //设置词元长度
            offsetAtt.setOffset(nextLexeme.getBeginPosition(),nextLexeme.getEndPosition()); //记录词元分类
            //记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            typeAtt.setType(nextLexeme.getLexemeText());
            return true; //告知还有下个词元
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }


    @Override
    public void end() throws IOException {
        int finalOffSet = correctOffset(this.endPosition);
        offsetAtt.setOffset(finalOffSet,finalOffSet);
    }
}
