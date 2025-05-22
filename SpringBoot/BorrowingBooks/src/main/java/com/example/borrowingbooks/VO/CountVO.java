package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountVO {
    private Integer Borrow; // 借
    private Integer Return; // 还
    private Integer Exceed; // 超期
}
