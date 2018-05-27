package kr.ac.dongeui.pangpang.data;

public class HelperData {

    private String name;
    private String email;
    private String img;
    private String token;

    public HelperData(String name, String email, String img, String token) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.token = token;
    }

    public HelperData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
