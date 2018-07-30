package tup.lucene.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tup.lucene.ik.IKAnalyzer6x;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HighlighterTest {
    public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
        String field = "title";
        Path indexPath = Paths.get("indexdir");
        Directory directory = FSDirectory.open(indexPath);
        IndexReader reader =DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new IKAnalyzer6x();
        QueryParser queryParser = new QueryParser(field,analyzer);
        Query query = queryParser.parse("北大");
        System.out.println("query:" + query);
        QueryScorer queryScorer = new QueryScorer(query,field);
        //定制高亮标签
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color:red;\">","</span>");
        Highlighter highlighter = new Highlighter(formatter,queryScorer);
        TopDocs topDocs = searcher.search(query,10);
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));
            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(),sd.doc,field,analyzer);
            //获取tokenStream
            Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);
            highlighter.setTextFragmenter(fragmenter);
            String str = highlighter.getBestFragment(tokenStream,doc.get(field));
            System.out.println("高亮的片段："+ str);

        }
        directory.close();
        reader.close();
    }
}
