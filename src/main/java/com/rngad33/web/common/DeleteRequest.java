package com.rngad33.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 通用删除请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteRequest {

    /**
     * id
     */
    private Long id;

}