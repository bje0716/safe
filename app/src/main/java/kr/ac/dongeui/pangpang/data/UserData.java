package kr.ac.dongeui.pangpang.data;

public class UserData {

   private String email;
   private String img;
   private String name;

    public UserData(String email, String img, String name) {
        this.email = email;
        this.img = img;
        this.name = name;
    }

    public UserData() {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
