package org.yesworkflow.save.data;

import com.google.gson.annotations.SerializedName;

public class RegisterDto
{
    @SerializedName("username")
    public String username;
    @SerializedName("email")
    public String email;
    @SerializedName("password1")
    public String password1;
    @SerializedName("password2")
    public String password2;

    public RegisterDto(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password1 = password;
        this.password2 = password;
    }

    public RegisterDto(Builder builder)
    {
        this.username = builder.username;
        this.email = builder.email;
        this.password1 = builder.password;
        this.password2 = builder.password;
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

        public RegisterDto build()
        {
            return new RegisterDto(this);
        }
    }
}
