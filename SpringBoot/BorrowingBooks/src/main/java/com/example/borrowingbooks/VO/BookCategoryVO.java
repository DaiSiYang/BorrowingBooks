package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCategoryVO {
    private Integer id;
    private String categoryName;
    private String description;
    private LocalDateTime createTime;
    private Integer bookCount;
}
