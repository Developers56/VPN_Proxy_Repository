package com.example.microvpn;

public class model_useInfo {
     String username;
     String useremail;
     String usepasswor;

    public model_useInfo(String username, String useremail, String usepasswor) {
        this.username = username;
        this.useremail = useremail;
        this.usepasswor = usepasswor;
    }

    public model_useInfo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsepasswor() {
        return usepasswor;
    }

    public void setUsepasswor(String usepasswor) {
        this.usepasswor = usepasswor;
    }
}
