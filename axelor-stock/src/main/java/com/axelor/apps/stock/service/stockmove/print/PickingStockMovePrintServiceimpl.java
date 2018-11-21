package com.axelor.apps.stock.service.stockmove.print;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.exception.IExceptionMessage;
import com.axelor.apps.stock.report.IReport;
import com.axelor.apps.tool.ModelTool;
import com.axelor.apps.tool.ThrowConsumer;
import com.axelor.apps.tool.file.PdfTool;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PickingStockMovePrintServiceimpl implements PickingStockMovePrintService {

  @Override
  public String printStockMoves(List<Long> ids) throws IOException {
    List<File> printedStockMoves = new ArrayList<>();
    ModelTool.apply(
        StockMove.class,
        ids,
        new ThrowConsumer<StockMove>() {
          @Override
          public void accept(StockMove stockMove) throws Exception {
            printedStockMoves.add(print(stockMove, ReportSettings.FORMAT_PDF));
          }
        });
    String fileName = getStockMoveFilesName(true, ReportSettings.FORMAT_PDF);
    return PdfTool.mergePdfToFileLink(printedStockMoves, fileName);
  }

  @Override
  public ReportSettings prepareReportSettings(StockMove stockMove, String format)
      throws AxelorException {
    if (stockMove.getPrintingSettings() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          String.format(
              I18n.get(IExceptionMessage.STOCK_MOVES_MISSING_PRINTING_SETTINGS),
              stockMove.getStockMoveSeq()),
          stockMove);
    }

    String locale = ReportSettings.getPrintingLocale(stockMove.getPartner());
    String title = getFileName(stockMove);

    ReportSettings reportSetting =
        ReportFactory.createReport(IReport.PICKING_STOCK_MOVE, title + " - ${date}");
    return reportSetting
        .addParam("StockMoveId", stockMove.getId())
        .addParam("Locale", locale)
        .addFormat(format);
  }

  @Override
  public File print(StockMove stockMove, String format) throws AxelorException {
    ReportSettings reportSettings = prepareReportSettings(stockMove, format);
    return reportSettings.generate().getFile();
  }

  @Override
  public String printStockMove(StockMove stockMove, String format)
      throws AxelorException, IOException {
    String fileName = getStockMoveFilesName(false, ReportSettings.FORMAT_PDF);
    return PdfTool.getFileLinkFromPdfFile(print(stockMove, format), fileName);
  }

  /**
   * Return the name for the printed stock move.
   *
   * @param plural if there is one or multiple stock moves.
   */
  public String getStockMoveFilesName(boolean plural, String format) {

    return I18n.get(plural ? "Stock moves" : "Stock move")
        + " - "
        + Beans.get(AppBaseService.class).getTodayDate().format(DateTimeFormatter.BASIC_ISO_DATE)
        + "."
        + format;
  }

  @Override
  public String getFileName(StockMove stockMove) {

    return I18n.get("Stock Move") + " " + stockMove.getStockMoveSeq();
  }
}
