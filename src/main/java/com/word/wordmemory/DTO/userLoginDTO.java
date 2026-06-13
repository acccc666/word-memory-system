package com.word.wordmemory.DTO;
import lombok.Data;
@Data
public class userLoginDTO {
        /**
         * 用户名 (对应 Apifox 里的 username)
         */
        private String username;

        /**
         * 密码 (对应 Apifox 里的 password)
         */
        private String password;
}
