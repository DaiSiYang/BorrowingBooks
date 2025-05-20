package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookVO {

    private Integer id;

    private String title;

    private String author;

    private String publisher;

    private LocalDate publishDate;

    private String isbn;

    private Integer categoryId;

    /**
     * 状态(1-可借阅,0-已借出,2-已损坏,3-已下架)
     */
    private Boolean status;

    private String location;

    private LocalDateTime createTime;
    private String coverImage;
}
