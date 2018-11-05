package utils;

import java.io.IOException;
import org.junit.Test;
import nerd.nlp.api.Document;

public class NLPUtilsTest {
    public void test_getDependencyParsing_NguyenXuanPhuc_sinhNgay() throws IOException {
        String sentence = "Ông sinh ngày 20 tháng 7 năm 1954, tại xã Quế Phú, huyện Quế Sơn, tỉnh Quảng Nam.";
        System.out.println(NLPUtils.getDependencyParsing(sentence));
    }
    
    public void test_getDependencyParsing_NguyenXuanPhuc_queO() throws IOException {
        String sentence = "Ông Nguyễn Xuân Phúc quê ở xã Quế Phú, huyện Quế Sơn, tỉnh Quảng Nam.";
        System.out.println(NLPUtils.getDependencyParsing(sentence));
    }
    
    public void test_getDependencyParsing_NguyenXuanPhuc_queBracket() throws IOException {
        String sentence = "Ông Nguyễn Xuân Phúc (quê ở Quảng Nam) sẽ đến thăm Thái Bình vào thời gian tới.";
        System.out.println(NLPUtils.getDependencyParsing(sentence));
    }
    
    public void test_getDependencyParsing_GiangSinhNgayQueTai() throws IOException {
        String sentence = "Ông Giang sinh ngày 12 tháng 10, quê tại Nghệ An.";
        System.out.println(NLPUtils.getDependencyParsing(sentence));
    }
    
    public void test_tagNER_ThuTuongNguyenXuanPhucThamTinhQuangNam() {
        String sentence = "Thủ tướng Nguyễn Xuân Phúc thăm tỉnh Quảng Nam.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    public void test_tagNER_TinhQuangNamLaNoiSinhCuaThuTuongNguyenXuanPhuc() {
        String sentence = "tỉnh Quảng Nam là nơi sinh của thủ tướng Nguyễn Xuân Phúc.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    public void test_tagNER_ThoiThoAuPhiNhung() {
        String sentence = "Trải qua thời thơ ấu với những chuỗi ngày vất vả và cay đắng ở mảnh đất Gia Lai, Phi Nhung thiệt thòi về mọi thứ. Trên sân khấu “Chuyện tối nay với Thành”, Trấn Thành hỏi Phi Nhung rằng, có phải vì trải qua tuổi thơ đầy cơ cực nên Phi Nhung muốn dành mọi thứ tốt đẹp nhất để các con của mình hạnh phúc?";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    public void test_tagNER_HuynhDucThoNullNerType() {
        String sentence = "Ông Huỳnh Đức Thơ - Chủ tịch UBND TP Đà Nẵng gọi dự án “ma” là vì tên dự án là khu du lịch, nhưng hơn 10 năm sau ngày giao đất, dự án vẫn án binh bất động. “Ma” là vì chủ dự án - tên chủ doanh nghiệp là Công ty I.V.C của Phan Văn Anh Vũ (trước đây) đã được chuyển đổi thành tên Lê Văn Sáu ở thời điểm 2015, dù ảnh thẻ chứng minh nhân dân đứng tên Lê Văn Sáu (khi nộp cùng giấy đăng ký kinh doanh) vẫn là hình ông Vũ “nhôm”?.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    public void test_tagNER_LeAnhXuanLocation() {
        String sentence = "Nhà thơ Lê Anh Xuân (1940-1968) tên thật là Ca Lê Hiến, quê Object, Bến Tre. Các tác phẩm thơ được chú ý của Subject gồm: Tiếng gà gáy (1965), Hoa dừa (1971), Tuyển thơ Lê Anh Xuân (1981)... Bài thơ cuối cùng của Lê Anh Xuân là bài Dáng đứng Việt Nam viết năm 1968. Lê Anh Xuân được truy tặng Giải thưởng Nhà nước về văn học nghệ thuật năm 2001.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    public void test_tagNER_NhacSiThanhTungOutOfIndex() {
        String sentence = "Nhạc sĩ Thanh Tùng sinh ngày 15/9/1948 tại Nha Trang, Khánh Hòa. Năm sáu tuổi, ông theo cha mẹ tập kết ra Bắc và lớn lên tại Hà Nội. Ông tốt nghiệp Nhạc viện Bình Nhưỡng, Triều Tiên năm 1971, khi mới 23 tuổi. Trở về nước, Subject đảm nhận vai trò chỉ huy dàn nhạc Đài Tiếng nói Việt Nam II từ 1971 tới 1975. Sau đó, ông vào sống tại TPHCM và là một trong những người có công xây dựng Dàn nhạc nhẹ Đài truyền hình TPHCM. Ông cũng từng chỉ huy hợp xướng và chỉ đạo nghệ thuật Đoàn Ca múa Bông Sen trước khi công tác tại Object.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
    
    @Test
    public void test_tagNER_NhaThoLeAnhXuanWrongType() {
        String sentence = "Nhà thơ Lê Anh Xuân (1940-1968) tên thật là Ca Lê Hiến, quê Châu Thành, Bến Tre. Các tác phẩm thơ được chú ý của Lê Anh Xuân gồm: Tiếng gà gáy (1965), Hoa dừa (1971), Tuyển thơ Lê Anh Xuân (1981)... Bài thơ cuối cùng của Lê Anh Xuân là bài Dáng đứng Việt Nam viết năm 1968. Lê Anh Xuân được truy tặng Giải thưởng Nhà nước về văn học nghệ thuật năm 2001.";
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        Document basicDocument = nlpUtils.tagNERBasic(sentence);
        System.out.println(document.getUnitList());
        Utils.printUnitList(document.getUnitList());
        System.out.println(basicDocument.getUnitList());
        Utils.printUnitList(basicDocument.getUnitList());
    }
}
