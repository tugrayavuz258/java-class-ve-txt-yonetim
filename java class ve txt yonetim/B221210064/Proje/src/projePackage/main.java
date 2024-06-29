/**
*
* @author Tuğra YAVUZ
* @since 03.07.2024
* <p>
* Repo klonlama işlemlerinni bulunduğu ve bu repodan çektiğim
* dosyaları analiz etmeye yarayan fonksiyonları içeren class
* bu class aynı zamanda main classtır
* </p>
*/



package projePackage;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projePackage.BilgiClass;
public class main {
	// Global bir String değişken tanımlıyoruz.
    public static String globalString = "";
    // BilgiClass türünden bir ArrayList tanımlıyoruz.
    static ArrayList<BilgiClass> bilgiListesi = new ArrayList<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  // Kullanıcıdan giriş almak için bir BufferedReader oluşturuyoruz.
        BufferedReader okuyucu = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Kullanıcıdan GitHub deposu bağlantısını istiyoruz.
            System.out.println("GitHub deposu bağlantısını girin:");
            String repoUrl = okuyucu.readLine();

            // Git klonlama işlemini gerçekleştirmek için bir ProcessBuilder oluşturuyoruz.
            ProcessBuilder cloneProcessBuilder = new ProcessBuilder("git", "clone", repoUrl);
            cloneProcessBuilder.directory(new File(System.getProperty("user.dir")));
            Process cloneProcess = cloneProcessBuilder.start();
            cloneProcess.waitFor();

            // Klonlanan repo dizinini alıyoruz.
            File clonedRepoDir = new File(getRepoName(repoUrl));
            // Dizin içindeki dosyaları işlemek için processDirectory metodunu çağırıyoruz.
            processDirectory(clonedRepoDir);

        } catch (IOException | InterruptedException e) { 
            // Hata durumunda istisna mesajını yazdırıyoruz.
            e.printStackTrace();
        } finally {
            try {
                // Okuyucuyu kapatıyoruz.
                okuyucu.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Bilgi listesini yazdırıyoruz.
        for (BilgiClass bilgi : bilgiListesi) {
            System.out.println(bilgi.toString());
        }
        // Klonlanan repo dizinini silmek için deleteDirectory metodunu çağırabiliriz.
        // Ancak şu an bu metodun çağrılma yerisi yok.
        // deleteDirectory(clonedRepoDir);
    }

    // Dizini işleyen özel bir metot
    private static void processDirectory(File dizin) throws IOException {
        // Dizindeki dosyaları alıyoruz
        File[] dosyalar = dizin.listFiles();
        if (dosyalar != null) {
            for (File dosya : dosyalar) {
                if (dosya.isDirectory()) {
                    // Eğer dosya bir dizinse, bu metodu yeniden çağırarak alt dizinleri işliyoruz.
                    processDirectory(dosya);
                } else if (dosya.isFile() && dosya.getName().endsWith(".java")) {
                    // Eğer dosya bir Java dosyasıysa işlem yapıyoruz.
                    String kodlar = dosyaIcerikOku(dosya);
                    if (containsClassDefinition(kodlar)) {
                        // Eğer dosya bir sınıf tanımı içeriyorsa, bilgileri topluyoruz.
                        double javadocSatirSayisi = javadocSatirSayisiHesapla(kodlar)+javadocSatirSayisiHesaplaAyniSatir(kodlar);
                        double yorumSatirSayisi = yorumSatirSayaci(kodlar)+yorumSatirSayaciAyniSatir(kodlar);
                        double LOC = kodlar.split("\r?\n").length;
                        double bosSatirSayisi = bosSatirSayisiBul(kodlar);
                        double kodSatirSayisi = LOC+yorumSatirSayaciAyniSatir(kodlar) +javadocSatirSayisiHesaplaAyniSatir(kodlar) - javadocSatirSayisi - yorumSatirSayisi - bosSatirSayisi - javadocIcinSayilmayanYorumSatirlariniBul(kodlar) - yorumIcinSayilmayanYorumSatirlariniBul(kodlar) + satirIciJavadocBul(kodlar) + kodIcindekiYorumSatirlariniBul(kodlar);
                        double fonksiyonSayisi = fonksiyonSayisiHesapla(kodlar);

                        // BilgiClass örneği oluşturup listeye ekliyoruz.
                        bilgiListesi.add(new BilgiClass(dosya.getName(), javadocSatirSayisi, yorumSatirSayisi, kodSatirSayisi, LOC, fonksiyonSayisi));
                    }
                }
            }
        }

    }//dosya içeriği okumaya yarayan metot
    private static String dosyaIcerikOku(File dosya) throws IOException {
        StringBuilder icerikOlusturucu = new StringBuilder();
        try (BufferedReader dosyaOkuyucu = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = dosyaOkuyucu.readLine()) != null) {
                icerikOlusturucu.append(satir).append("\n");
            }
        }
        return icerikOlusturucu.toString();
    }
//fonksiyon sayısını hesaplayan metot
    private static int fonksiyonSayisiHesapla(String kod) {
        Pattern pattern = Pattern.compile("(public|private|protected)\\s+([\\w\\[\\]]+\\s+)*\\w+\\s*\\([^)]*\\)\\s*\\{");
        Matcher matcher = pattern.matcher(kod);
        int say = 0;
        boolean kurucumu = false;
        while (matcher.find()) {
            String fonksiyonAdi = kod.substring(matcher.start(), matcher.end()).split("\\(")[0].trim();
            if (!kurucumu && fonksiyonAdi.equals(getClassName(kod))) {
                kurucumu = true;
            } else {
                say++;
            }
        }
        return say;
    }
    //asagidaki javadoc ve yorum satırı sayan fonksiyonları cesitli ihtiyaclarim icin birbirinden turettim
    private static int javadocSatirSayisiHesapla(String kod) {
        String[] satirlar = kod.split("\n");
        int say = 0;
        boolean JavadocIcindeMi = false;

        for (String satir : satirlar) {
            satir = satir.trim();
            if (satir.contains("/**") && !satir.contains("*/")) {
                JavadocIcindeMi = true;
               // if(!satir.substring(satir.indexOf("/**") + 3).trim().isEmpty())
                	//{say++;} 
            } else if (JavadocIcindeMi) {
                if (satir.contains("*/")) {
                    JavadocIcindeMi = false;
                }
                if (!satir.isEmpty() && !satir.contains("/**") && !satir.contains("*/")) {
                    say++;
                }
            } else if (satir.contains("/**") && satir.contains("*/")) {
                String aradakiIcerik = kod.substring(kod.indexOf("/*") + 3, kod.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    say++;
                }
            }
        }
        return say;
    }
    
    private static int javadocSatirSayisiHesaplaAyniSatir(String kod) {
        String[] satirlar = kod.split("\n");
        int say = 0;
        boolean JavadocIcindeMi = false;

        for (String satir : satirlar) {
            satir = satir.trim();
            if (satir.contains("/**") && !satir.contains("*/")) {
                JavadocIcindeMi = true;
                if(!satir.substring(satir.indexOf("/**") + 3).trim().isEmpty())
                	{say++;} 
            } else if (JavadocIcindeMi) {
            	if (satir.contains("*/")) {
            		JavadocIcindeMi=false;
            	    int index = satir.indexOf("*/");
            	    String oncesi = satir.substring(0, index); // */ ifadesinden önceki kısmı al
            	    if (!oncesi.trim().isEmpty()) {
            	        // Önceki kısım dolu ise
            	        say++;
            	    }
            	}
                if (!satir.isEmpty() && !satir.contains("/**") && !satir.contains("*/")) {
                    
                }
            } else if (satir.contains("/**") && satir.contains("*/")) {
                String aradakiIcerik = kod.substring(kod.indexOf("/*") + 3, kod.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    
                }
            }
        }
        return say;
    }

    public static int yorumSatirSayaci(String kod) {
        int yorumSatirSayisi = 0;
        boolean yorumAcik = false;
        String[] kodSatirlari = kod.split("\n");

        for (String satir : kodSatirlari) {
            satir = satir.trim();

            if (satir.contains("//") && !satir.trim().substring(satir.indexOf("//") + 2).isEmpty()) {
                yorumSatirSayisi++;
            } else if (satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                yorumAcik = true;
                //if(!satir.substring(satir.indexOf("/*") + 2).trim().isEmpty())
            	//{yorumSatirSayisi++;} 
                
            } else if (yorumAcik) {
                if (satir.contains("*/")) {
                    yorumAcik = false;
                }
                if (!satir.isEmpty() && !satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                    yorumSatirSayisi++;
                }
            } else if (satir.contains("/*") && satir.contains("*/") && !satir.contains("/**")) {
                String aradakiIcerik = satir.substring(satir.indexOf("/*") + 2, satir.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    yorumSatirSayisi++;
                }
            }
        }
        return yorumSatirSayisi;
    }
    
    public static int yorumSatirSayaciAyniSatir(String kod) {
        int yorumSatirSayisi = 0;
        boolean yorumAcik = false;
        String[] kodSatirlari = kod.split("\n");

        for (String satir : kodSatirlari) {
            satir = satir.trim();

            if (satir.contains("//")) {
                
            } else if (satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                yorumAcik = true;
                if(!satir.substring(satir.indexOf("/*") + 2).trim().isEmpty())
            	{yorumSatirSayisi++;} 
                
            } else if (yorumAcik) {
            	if (satir.contains("*/")) {
            		yorumAcik=false;
            	    int index = satir.indexOf("*/");
            	    String oncesi = satir.substring(0, index); // */ ifadesinden önceki kısmı al
            	    if (!oncesi.trim().isEmpty()) {
            	        // Önceki kısım dolu ise
            	    	yorumSatirSayisi++;
            	    }
            	}
                if (!satir.isEmpty() && !satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                   
                }
            } else if (satir.contains("/*") && satir.contains("*/") && !satir.contains("/**")) {
                String aradakiIcerik = satir.substring(satir.indexOf("/*") + 2, satir.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    
                }
            }
        }
        return yorumSatirSayisi;
    }

    public static int javadocIcinSayilmayanYorumSatirlariniBul(String kod) {
        String[] satirlar = kod.split("\n");
        int say = 0;
        boolean javadocIcindeMi = false;

        for (String satir : satirlar) {
            satir = satir.trim();
            if (satir.contains("/**") && !satir.contains("*/")) {
                javadocIcindeMi = true;
            } else if (javadocIcindeMi) {
                if (satir.contains("*/")) {
                    javadocIcindeMi = false;
                    say = say + 2;
                }
                if (!satir.isEmpty() && !satir.contains("/**") && !satir.contains("*/")) {
                }
            } else if (satir.contains("/**") && satir.contains("*/")) {
            }
        }
        return say;
    }

    public static int yorumIcinSayilmayanYorumSatirlariniBul(String kod) {
        int yorumSatirSayisi = 0;
        boolean yorumAcik = false;
        String[] kodSatirlari = kod.split("\n");

        for (String satir : kodSatirlari) {
            satir = satir.trim();

            if (satir.contains("//")) {
            } else if (satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                yorumAcik = true;
            } else if (yorumAcik) {
                if (satir.contains("*/")) {
                    yorumAcik = false;
                    yorumSatirSayisi = yorumSatirSayisi + 2;
                }
                if (!satir.isEmpty() && !satir.contains("/*") && !satir.contains("*/") && !satir.contains("/**")) {
                }
            } else if (satir.contains("/*") && satir.contains("*/") && !satir.contains("/**")) {
            }
        }

        return yorumSatirSayisi;
    }

    public static int kodIcindekiYorumSatirlariniBul(String kod) {
        int yorumSatirSayisi = 0;
        String[] kodSatirlari = kod.split("\n");

        for (String satir : kodSatirlari) {
            satir = satir.trim();

            if (!satir.startsWith("//") && satir.contains("//")) {
                yorumSatirSayisi++;
            } else if (!satir.startsWith("/*") && satir.contains("/*") && satir.contains("*/") && !satir.contains("/**")) {
                String aradakiIcerik = satir.substring(satir.indexOf("/*") + 2, satir.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    yorumSatirSayisi++;
                }
            }
        }

        return yorumSatirSayisi;
    }

    public static int satirIciJavadocBul(String kod) {
        String[] satirlar = kod.split("\n");
        int say = 0;

        for (String satir : satirlar) {
            satir = satir.trim();

            if (!satir.startsWith("/**") && satir.contains("/**") && satir.contains("*/")) {
                String aradakiIcerik = kod.substring(kod.indexOf("/*") + 3, kod.indexOf("*/")).trim();
                if (!aradakiIcerik.isEmpty()) {
                    say++;
                }
            }
        }
        return say;
    }
    //bos satir saysini regex ifade ile bulan kod
    public static int bosSatirSayisiBul(String kod) {
        String[] satirlar = kod.split("\r?\n");
        int bosSatirSayisi = 0;

        for (String satir : satirlar) {
            if (satir.trim().isEmpty()) {
                bosSatirSayisi++;
            }
        }

        return bosSatirSayisi;
    }
//class tanimi yakalayan kod blogu
    private static boolean containsClassDefinition(String content) {
        return content.contains("class");
    }

    private static String getRepoName(String repoUrl) {
        String[] parcalar = repoUrl.split("/");
        return parcalar[parcalar.length - 1].split("\\.")[0];
    }
    //directory yi silmeye yarayan modül
    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    } 
    private static String getClassName(String kod) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(kod);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
	}

}
