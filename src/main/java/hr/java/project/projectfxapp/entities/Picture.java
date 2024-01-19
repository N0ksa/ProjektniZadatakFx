package hr.java.project.projectfxapp.entities;

public class Picture {

    private String picturePath;

    public Picture(String picturePath) {
        this.picturePath = picturePath;
    }


    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
