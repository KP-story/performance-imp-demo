package test;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
@Getter
public class ExcelReadingServiceImpl implements ExcelReadingService {
    private final TextRepo textRepo;
    private final SequenceIdRepo sequenceIdRepo;
    private  LinkedBlockingQueue linkedBlockingQueue= new LinkedBlockingQueue<Runnable>(10);
    private ExecutorService executorService=         new ThreadPoolExecutor(8, 8,
                                      0L,TimeUnit.MILLISECONDS,linkedBlockingQueue);

    private Workbook getWorkbook(InputStream inputStream) throws IOException {
//        return WorkbookFactory.create(inputStream);
        return StreamingReader.builder()
                .rowCacheSize(1000)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(inputStream);

    }

    public List<String> getDataFromExcel(Workbook workbook) throws Exception {
        List<String> rowValueList = new ArrayList<>();
        AtomicLong atomicLong= new AtomicLong(0);
        final Sheet sheet = workbook.getSheetAt(0);
        final Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String dataFromRow = getDataFromRow(row);
            rowValueList.add(dataFromRow);
            if (rowValueList.size() == 1000) {
             long batch=   atomicLong.incrementAndGet();
             List rows=rowValueList;
             while (linkedBlockingQueue.size()>=10)
             {
                 Thread.sleep(100);
             }
                insert(rows,batch);
                rowValueList = new ArrayList<>(1000);
            }
        }
        if (!rowValueList.isEmpty()) {
            long batch=   atomicLong.incrementAndGet();
            insert(rowValueList,batch);
        }
        return rowValueList;
    }

    private String getDataFromRow(Row row) {
        DataFormatter formatter = new DataFormatter();
        Cell cell = row.getCell(0);
        return formatter.formatCellValue(cell).trim();
    }

    public void insert(List<String> rowValues,long atomicLong) {
        executorService.execute(() -> {
            List<TextEntity> textEntities = new ArrayList<>();
            List<Long> ids = sequenceIdRepo.getNextContactIds(rowValues.size());
            for (int i = 0; i < rowValues.size(); i++) {

                TextEntity textEntity = new TextEntity();
                textEntity.setId(ids.get(i));
                textEntity.setText(rowValues.get(i));
                textEntities.add(textEntity);
            }
            long timeNow=System.currentTimeMillis();
            textRepo.saveAll(textEntities);
            System.out.println("finish import "+atomicLong+"time: "+(System.currentTimeMillis()-timeNow));
        });

    }

    @Override
    public void insert(InputStream inputStream) throws Exception {
        Workbook workbook = getWorkbook(inputStream);
        getDataFromExcel(workbook);
    }
}
