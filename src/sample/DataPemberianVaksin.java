package sample;

public class DataPemberianVaksin {
    String noTiket;
    String noNIK;
    String nama;
    String tglLahir;
    String noTelp;

    public DataPemberianVaksin(String noTiket, String noNIK, String nama, String tglLahir, String noTelp) {
        this.noTiket = noTiket;
        this.noNIK = noNIK;
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.noTelp = noTelp;
    }

    @Override
    public String toString() {
        return "DataPemberianVaksin{" +
                "noTiket='" + noTiket + '\'' +
                ", noNIK='" + noNIK + '\'' +
                ", Nama='" + nama + '\'' +
                ", tglLahir='" + tglLahir + '\'' +
                ", noTelp='" + noTelp + '\'' +
                '}';
    }
}
