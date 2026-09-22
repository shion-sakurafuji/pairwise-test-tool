package io.github;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import io.github.model.Factor;
import io.github.service.PairwiseGenerator;
import io.github.service.CsvGenerator;
import org.springframework.ui.Model;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";    
    }

    @PostMapping("/generate")
    public Object generate(HttpServletRequest request, Model model) {
        //index.htmlで入力した因子の配列
        String[] factorNames = request.getParameterValues("factorName");
        //index.htmlで入力した水準の文字列(,含む)を各因子単位で入力された文字列の配列
        String[] levels = request.getParameterValues("levels");
        
        //因子が2個未満
        if (factorNames == null || factorNames.length < 2){
            model.addAttribute("error","因子は2個以上入力してください。");
            return "index";
        }

        //後々で作成するFactorオブジェクトにて、ArrayListクラスでインスタンス化
        List<Factor> factors = new ArrayList<>();

        for (int i = 0; i < factorNames.length; i++) {
            //上列で初期化した配列（因子）添字iについて文字列factorNameとする
            String factorName = factorNames[i];
            if (factorName.isBlank()) {
                model.addAttribute("error","因子名を入力してください。");
                return "index";
            }

            //上列で初期化した配列（水準）添字iについて、配列levelArrayとする（,で別れた単語単位で添え字がふられる）
            String[] levelArray = levels[i].split(",");
            //水準が2個未満
            if (levelArray.length < 2 ){
                model.addAttribute("error","各因子には水準を2個以上入力してください。");
                return "index";
            }

            //String型のListとして、levelListを作成する。
            List<String> levelList = new ArrayList<>();

            //拡張for文。配列levelArray（水準）に関して、levelListにしまう
            for (String level : levelArray) {
                levelList.add(level.trim());
            }

            Factor factor = new Factor(factorName,levelList);
            //Factor型のfactorに代入。代入するのは実引数、文字列のFactornameと、リストのlevelListでインスタンス化したオブジェクト
            //
            factors.add(factor);
        }
        
        //generatorにPairwiseGeneratorをインスタンス化して代入
        PairwiseGenerator generator = new PairwiseGenerator();
        // generatorオブジェクトのgenerateメソッドへ実引数factorsを渡し、
        //戻り値をtestCasesに代入
        List<List<String>> testCases = generator.generate(factors);
        CsvGenerator csvGenerator = new CsvGenerator();
        String csv = csvGenerator.generate(factors, testCases);
    
        // Excelなどで日本語が文字化けしにくいようUTF-8 BOMを付ける
        byte[] csvBytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"pairwise-test-cases.csv\"")
        .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
        .body(csvBytes);
    }
}