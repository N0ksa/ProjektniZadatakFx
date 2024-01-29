package hr.java.project.projectfxapp.entities;

import java.util.Objects;

public final class Picture {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Objects.equals(picturePath, picture.picturePath);
    }
    @Override
    public int hashCode() {
        return Objects.hash(picturePath);
    }
}
