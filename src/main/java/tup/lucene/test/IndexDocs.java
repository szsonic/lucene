package tup.lucene.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.util.ResourceUtils;
import tup.lucene.ik.IKAnalyzer6x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class IndexDocs {
    public static void main(String[] args) throws IOException {
        File newsFile = ResourceUtils.getFile("classpath:file/new.txt");
        String text = fileToText(newsFile);
        Analyzer analyzer = new IKAnalyzer6x(true);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //索引的存储路径
        Directory directory = null;
        //索引的增删改由indexWrite创建
        IndexWriter indexWriter = null;
        directory = FSDirectory.open(Paths.get("indexdir"));
        indexWriter = new IndexWriter(directory,iwc);
        //新建FieldType，用于指定字段索引时的信息
        FieldType fieldType = new FieldType();
        //索引时保存文档，词项频率，位置信息，偏移信息
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        //原始字符串全部被保存在索引中
        fieldType.setStored(true);
        //存储词向量
        fieldType.setStoreTermVectors(true);
        //词条化
        fieldType.setTokenized(true);
        Document doc1 = new Document();
        Field field = new Field("content",text,fieldType);
        doc1.add(field);
        indexWriter.addDocument(doc1);
        indexWriter.close();
        directory.close();

    }
    public static String fileToText(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String str = null;
            while ((str = reader.readLine())!=null){
                result.append(System.lineSeparator() + str);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return result.toString();
    }
}
