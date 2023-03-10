package com.gugumin.halo.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Account request.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String username;
    private String password;
}
