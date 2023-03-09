package com.gugumin.halo.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String username;
    private String password;
}
