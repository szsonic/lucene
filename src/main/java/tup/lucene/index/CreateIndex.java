package tup.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tup.lucene.ik.IKAnalyzer6x;
import tup.lucene.model.News;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class CreateIndex {
    public static void main(String[] args) throws IOException {
        News news1 = new News();
        news1.setId(1);
        news1.setTitle("习近平会见美国总统奥巴马，学习国外经验");
        news1.setContent("国家主席习近平9月3日在杭州西湖国宾馆会见前来出席二十国集团领导人杭州峰会的美国总统奥巴马。。。");
        news1.setReply(672);
        News news2 = new News();
        news2.setId(2);
        news2.setTitle("北大迎4380名新生 农村学生700多人近年最多");
        news2.setContent("昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生工700多名，为近年最多...");
        news2.setReply(995);
        News news3 = new News();
        news3.setId(3);
        news3.setTitle("特朗普宣誓(Donald Trump)就任美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        news3.setReply(1872);

        //创建IK分词器
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig icw = new IndexWriterConfig(analyzer);
        icw.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = null;
        IndexWriter indexWriter = null;
        Path indexPath = Paths.get("indexdir");
        //开始时间
        Date start = new Date();
        try {
            if(!Files.isReadable(indexPath)){
                System.out.println("Document directory '"+indexPath.toAbsolutePath() +"'does not exist or is not readable,please check the path");
                System.exit(1);
            }
            dir = FSDirectory.open(indexPath);
            indexWriter = new IndexWriter(dir,icw);
            //设置新闻ID索引存储
            FieldType idType = new FieldType();
            idType.setIndexOptions(IndexOptions.DOCS);
            idType.setStored(true);
            //设置新闻标题索引文档、词项频率、位移信息和偏移量，存储并词条化
            FieldType titleType = new FieldType();
            titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            titleType.setStored(true);
            titleType.setTokenized(true);

            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            contentType.setTokenized(true);
            contentType.setStored(true);
            contentType.setStoreTermVectorOffsets(true);
            contentType.setStoreTermVectorPayloads(true);
            contentType.setStoreTermVectorPositions(true);
            contentType.setStoreTermVectors(true);

            Document doc1 = new Document();
            doc1.add(new Field("id",String.valueOf(news1.getId()),idType));
            doc1.add(new Field("title",String.valueOf(news1.getTitle()),titleType));
            doc1.add(new Field("content",String.valueOf(news1.getContent()),contentType));
            doc1.add(new IntPoint("reply",news1.getReply()));
            doc1.add(new StoredField("reply_display",news1.getReply()));
            Document doc2 = new Document();
            doc2.add(new Field("id",String.valueOf(news2.getId()),idType));
            doc2.add(new Field("title",String.valueOf(news2.getTitle()),titleType));
            doc2.add(new Field("content",String.valueOf(news2.getContent()),contentType));
            doc2.add(new IntPoint("reply",news2.getReply()));
            doc2.add(new StoredField("reply_display",news2.getReply()));
            Document doc3 = new Document();
            doc3.add(new Field("id",String.valueOf(news3.getId()),idType));
            doc3.add(new Field("title",String.valueOf(news3.getTitle()),titleType));
            doc3.add(new Field("content",String.valueOf(news3.getContent()),contentType));
            doc3.add(new IntPoint("reply",news3.getReply()));
            doc3.add(new StoredField("reply_display",news3.getReply()));

            indexWriter.addDocument(doc1);
            indexWriter.addDocument(doc2);
            indexWriter.addDocument(doc3);
            indexWriter.commit();
            indexWriter.close();
            dir.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("索引文档用时："+(System.currentTimeMillis() - start.getTime())+"ms");
    }
}
