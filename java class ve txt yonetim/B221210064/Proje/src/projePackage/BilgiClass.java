/**
*
* @author Tuğra YAVUZ
* @since 03.07.2024
* <p>
* Ekrana yazdırılacak değerleri nesnelerde tutmamı sağlayacak
* sınıf budur. Bu sınıf sayesinde verileri düzenli şekilde saklayabiliyorum.
* </p>
*/





package projePackage;

public class BilgiClass {
	private String sinifAdi;
	private double javaDOCsatirSayisi;
	private double digerYorumlarSatirSayi;
	private double kodSatirSayi;
	private double DosyadakiHerŞeyDahilSatirSayisi;
	private double fonksiyonSayisi;
	private double YorumSapmaYuzde;
	
	public BilgiClass(String sinifAdi, double javaDOCsatirSayisi, double digerYorumlarSatirSayi, double kodSatirSayi, double dosyadakiHerŞeyDahilSatirSayisi, double fonksiyonSayisi)
	{
		  this.sinifAdi = sinifAdi;
		    this.javaDOCsatirSayisi = javaDOCsatirSayisi;
		    this.digerYorumlarSatirSayi = digerYorumlarSatirSayi;
		    this.kodSatirSayi = kodSatirSayi;
		    this.DosyadakiHerŞeyDahilSatirSayisi = dosyadakiHerŞeyDahilSatirSayisi;
		    this.fonksiyonSayisi=fonksiyonSayisi;
		    
		    double YGa = ((javaDOCsatirSayisi + digerYorumlarSatirSayi) * 0.8) / fonksiyonSayisi;
		    double YHa = (kodSatirSayi / fonksiyonSayisi) * 0.3;
		    this.YorumSapmaYuzde = (double)(((100 * YGa) / YHa) - 100); 
	}	
	
	
	public double getFonksiyonSayisi() {
	    return fonksiyonSayisi;
	}

	public void setFonksiyonSayisi(int fonksiyonSayisi) {
	    this.fonksiyonSayisi = fonksiyonSayisi;
	}
	
	public String getSinifAdi() {
        return sinifAdi;
    }

	public void setSinifAdi(String sinifAdi) {
        this.sinifAdi = sinifAdi;
    }
	public double getJavaDOCsatirSayisi() {
        return javaDOCsatirSayisi;
    }

    public void setJavaDOCsatirSayisi(int javaDOCsatirSayisi) {
        this.javaDOCsatirSayisi = javaDOCsatirSayisi;
    }
	
    public double getDigerYorumlarSatirSayi() {
        return digerYorumlarSatirSayi;
    }

    public void setDigerYorumlarSatirSayi(int digerYorumlarSatirSayi) {
        this.digerYorumlarSatirSayi = digerYorumlarSatirSayi;
    }
    
    public double getKodSatirSayi() {
        return kodSatirSayi;
    }

    public void setKodSatirSayi(int kodSatirSayi) {
        this.kodSatirSayi = kodSatirSayi;
    }
    
    public double getDosyadakiHerŞeyDahilSatirSayisi() {
        return DosyadakiHerŞeyDahilSatirSayisi;
    }

    public void setDosyadakiHerŞeyDahilSatirSayisi(int dosyadakiHerŞeyDahilSatirSayisi) {
        this.DosyadakiHerŞeyDahilSatirSayisi = dosyadakiHerŞeyDahilSatirSayisi;
    }

    public double getYorumSapmaYuzde() {
        return YorumSapmaYuzde;
    }

    public void setYorumSapmaYuzde(int yorumSapmaYuzde) {
        this.YorumSapmaYuzde = yorumSapmaYuzde;
    }
    
    @Override
    public String toString() {
        return "sinif adi: " + this.sinifAdi + "\n" + 
               "JavaDocSatirSayisi: " + this.javaDOCsatirSayisi + "\n" + 
               "Diğer yorum satirlari: " + this.digerYorumlarSatirSayi + "\n" + 
               "Kod Satir Sayisi: " + this.kodSatirSayi + "\n" + 
               "LOC: " + this.DosyadakiHerŞeyDahilSatirSayisi + "\n" + 
               "Fonksiyon Sayisi " + this.fonksiyonSayisi + "\n" + 
               "Yorum Sapma Yuzdesi: %" + String.format("%.2f", this.YorumSapmaYuzde) + "\n" + 
               "------------------------------";
    }
    
    
}

