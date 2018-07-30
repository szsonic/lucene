package tup.lucene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class News {
    private int id;
    private String title;
    private String content;
    private int reply;

}
