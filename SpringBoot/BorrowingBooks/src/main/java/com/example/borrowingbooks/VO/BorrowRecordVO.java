package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordVO {
    private String Image;
    private String isbn;
    private String title;
    private String author;
    private String username;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Integer status;
}
