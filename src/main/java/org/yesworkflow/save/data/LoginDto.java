package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class LoginDto
{
    @SerializedName("username")
    public String username;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public LoginDto(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public LoginDto(Builder builder)
    {
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
    }

    public static class Builder
    {
        public String username;
        public String email;
        public String password;

        public Builder(String username, String password)
        {
            this.username = username;
            this.password = password;
        }

        public Builder setEmail(String email)
        {
            this.email = email;
            return this;
        }

        public LoginDto build()
        {
            return new LoginDto(this);
        }
    }
}
