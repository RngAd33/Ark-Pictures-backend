package com.rngad33.web.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求体
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 3191241716373120793L;

}