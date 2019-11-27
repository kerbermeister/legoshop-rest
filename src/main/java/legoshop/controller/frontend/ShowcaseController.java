package legoshop.controller.frontend;

import legoshop.domain.Part;
import legoshop.service.BlobDecoder;
import legoshop.service.PartService;
import legoshop.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Контроллер витрины. Отвечает за обработку запросов с id типа на /types
 */

@Controller
@RequestMapping("/category")
public class ShowcaseController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private PartService partService;

    @Autowired
    private BlobDecoder blobDecoder;


    @RequestMapping(method = RequestMethod.GET, value = "/{typeId}")
    public String getTypeParts(@PathVariable Long typeId,
                               @RequestParam (defaultValue = "0") int page,
                               @RequestParam (defaultValue = "3") Integer size,
                               @RequestParam (defaultValue = "eng_name") String sort,
                               @RequestParam (defaultValue = "desc") String order,
                               Model model) {

        Page<Part> pagedParts = partService.findPartsByType(typeId, page, size, sort, order);
        int totalPages = pagedParts.getTotalPages();
        List<Part> pagedPartList = pagedParts.getContent();

        model.addAttribute("parts", pagedPartList);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("typeId", typeId);
        }

        List<String> base64List = blobDecoder.getBase64List(pagedPartList);
        model.addAttribute("images", base64List);
        return "showcase";
    }
}
