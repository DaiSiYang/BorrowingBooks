package com.example.borrowingbooks.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private Integer currentPage;
    private Integer pageSize;
}
