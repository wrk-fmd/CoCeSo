package at.wrk.coceso.utils;

import com.itextpdf.text.Font;

public class PdfStyle {

  public static final Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
  public static final Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 18);

  public static final Font title2Font = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
  public static final Font descrFont = new Font(Font.FontFamily.HELVETICA, 12);

  public static final Font defFont = new Font(Font.FontFamily.HELVETICA, 11);

  private PdfStyle() {
  }

}
