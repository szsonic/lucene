package tup.lucene.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tup.lucene.ik.IKAnalyzer6x;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QueryParseTest {
    public static void main(String[] args) throws IOException, ParseException {
        String field = "title";
        Analyzer analyzer = new IKAnalyzer6x();
        QueryParser parser = new QueryParser(field, analyzer);
        parser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = parser.parse("北京大学");
        System.out.println("========普通搜索======");
        query(query);

        //多域搜索
        String[] fields = {"title", "content"};
        analyzer = new IKAnalyzer6x(true);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields,analyzer);
        query = multiFieldQueryParser.parse("美国");
        System.out.println("========多域搜索======");
        query(query);

        //词项搜索
        //词项搜索不能使用分词器，所以最好搜索不分词的字段
        System.out.println("========词项搜索======");
        Term term = new Term("title","美国");
        query = new TermQuery(term);
        query(query);

        //布尔搜索
        System.out.println("========布尔搜索======");
        Query query1 = new TermQuery(new Term("title","美国"));
        Query query2 = new TermQuery(new Term("title","习近平"));
        BooleanClause bc1 = new BooleanClause(query1,BooleanClause.Occur.MUST);
        BooleanClause bc2 = new BooleanClause(query2,BooleanClause.Occur.MUST_NOT);
        BooleanQuery booleanQuery = new  BooleanQuery.Builder().add(bc1).add(bc2).build();
        query(booleanQuery);

        //范围搜索
        System.out.println("========范围搜索======");
        query = IntPoint.newRangeQuery("reply",1,2000);
        query(query);

        //前缀搜索
        System.out.println("=======前缀搜索=======");
        term = new Term("title","学生");
        query = new PrefixQuery(term);
        query(query);

        //多关键字搜索
        System.out.println("=======多关键字搜索=====");
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("title","宣誓"),2);
        builder.add(new Term("title","美国"),3);
        query(builder.build());

        //模糊搜索
        System.out.println("======模糊搜索=======");
        query = new FuzzyQuery(new Term("title","turmp"));
        query(query);

        //通配符搜索
        System.out.println("======通配符搜索======");
        query = new WildcardQuery(new Term(field,"北?"));
        query(query);
    }

    public static void query(Query query) throws IOException {
        Path indexPath = Paths.get("indexdir");
        Directory dir = FSDirectory.open(indexPath);
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        System.out.println("Query：" + query.toString());
        TopDocs tds = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : tds.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println("DocId:" + scoreDoc.doc);
            System.out.println("id:" + document.get("id"));
            System.out.println("title:" + document.get("title"));
            System.out.println("reply:" + document.get("reply_display"));
            System.out.println("文档评分:" + scoreDoc.score);
        }
        dir.close();
        indexReader.close();
    }
}
