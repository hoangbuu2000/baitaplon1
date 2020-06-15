/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.baitaplon;

import static demo.baitaplon.Tester.getConnection;
import static demo.baitaplon.Tester.getIdUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Buu
 */
public class QuestionManagement {
    private List<Question> questions = new ArrayList<>();
    
    public void addQuestion(Question q){
        this.getQuestions().add(q);
    }
    
    public List<Question> getMultipleChoiceQ(){
        List<Question> k = new ArrayList<>();
        for(Question q : getQuestions()){
            if(q instanceof MultipleChoice)
                k.add(q);
        }
        return k;
    }
    
    public List<Question> getInCompleteQ(){
        List<Question> k = new ArrayList<>();
        for(Question q : questions){
            if(q instanceof InComplete)
                k.add(q);
        }
        return k;
    }
    
    public List<Question> getConversationQ(){
        List<Question> k = new ArrayList<>();
        for(Question q : questions){
            if(q instanceof Conversation)
                k.add(q);
        }
        return k;
    }
    
    public void practiceMultipleC(Scanner scanner, int n, User u) throws ClassNotFoundException, SQLException{
        Collections.shuffle(this.getMultipleChoiceQ());
        List<Question> correctQ = new ArrayList<>();
        List<Question> inCorrectQ = new ArrayList<>();
        String[] notes = new String[100];
        String[] kws = new String[100];
        scanner.nextLine();
        List<Double> score = new ArrayList<>();
        List<Question> dQuest = new ArrayList<>();
        for(int i = 0; i < n; i++){
            
            System.out.println(this.getMultipleChoiceQ().get(i));
            System.out.print("Answer: ");
            kws[i] = scanner.nextLine();
            
            MultipleChoice c = (MultipleChoice) this.getMultipleChoiceQ().get(i);
            for(int j = 0; j < c.getChoices().size(); j++){
                if(c.getChoices().get(j).isCorrect())
                    notes[i] =  String.format("The correct answer is: %s\nNote: %s\n", c.getLABELS()[j], c.getChoices().get(j).getNote());
            }
            
            if(this.getMultipleChoiceQ().get(i).checkAnswer(kws[i])){
                score.add(1.0);
                correctQ.add(this.getMultipleChoiceQ().get(i));
            }
            else{
                score.add(0.0);
                inCorrectQ.add(this.getMultipleChoiceQ().get(i));
            }
            
            dQuest.add(this.getMultipleChoiceQ().get(i));
        }
        //Neu so luong lam bai kiem tra = 0 thi tang len 1, nguoc lai tiep tuc tang tiep so luong lam bai kiem tra 1 don vi
        if(u.getNumberOfTests(Tester.getConnection()) != 0)
            u.setCountTest(u.getNumberOfTests(Tester.getConnection()) + 1);
        else
            u.setCountTest(1);
        u.setScore(score);
        u.setDoneQuestions(dQuest);
        
        System.out.println("\n========== RESULT ==========");
        for(int i = 0; i < n ;i++){
            System.out.println(this.getMultipleChoiceQ().get(i));
            System.out.println("The answer of user: " + kws[i].toUpperCase());
            System.out.println(notes[i]);
        }
    }
    
    /**
     * Phuong thuc luyen tap MultipleChoice truy xuat tu CSDL
     * @param scanner
     * @param n so luong cau hoi nguoi dung muon luyen tap
     * @param u nguoi dung hien tai dang dang nhap
     * @param conn ket noi CSDL
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException 
     */
    public void practiceMultipleC(Scanner scanner, int n, User u, Connection conn) throws ClassNotFoundException, SQLException, ParseException{
        Statement stm = conn.createStatement();
        Statement stm1 = conn.createStatement();
        
        ResultSet rs = stm.executeQuery("SELECT m.id, m.content as question,"
                    + " c.content as category, l.content as level"
                    + " FROM multiplechoice m"
                    + " INNER JOIN level l ON m.level_id = l.id"
                    + " INNER JOIN category c ON m.category_id = c.id"
                    + " WHERE m.incomplete_id IS NULL AND m.conversation_id IS NULL"
                    + " AND m.id NOT IN(SELECT question_id FROM practice"
                    + " INNER JOIN user ON user.id = practice.user_id WHERE username = '" + u.getUsername() + "')" 
                    + " ORDER BY RAND() LIMIT " + n);
        QuestionManagement questions = new QuestionManagement();
        int multipleChoiceId = 0;
        int[] questionId = new int[n]; // Mang luu tru cac id cau hoi
        int j = 0;
        while(rs.next()){ // Duyet tung cau hoi lay tu csdl
            Level level = new Level(rs.getString("level"));
            Category category = new Category(rs.getString("category"));
            Question q = new MultipleChoice(rs.getString("question"), level, category);
            multipleChoiceId = rs.getInt("id");
            questionId[j] = rs.getInt("id");
            j++;
            ResultSet rs1 = stm1.executeQuery("SELECT * FROM choice"
                        + " WHERE choice.multiplechoice_id = " + multipleChoiceId);
            while(rs1.next()){ // Duyet de lay cac cau tra loi add vao cau hoi
                Choice ch = new Choice(rs1.getString("content"), 
                            rs1.getBoolean("correct"), rs1.getString("note"));
                q.addChoice(ch);
            }
            questions.addQuestion(q);
            rs1.close();
        }
        rs.close();
        questions.practiceMultipleC(scanner, n, u);
                     
        String query = "INSERT INTO practice(user_id, question_id, type, date_practice, quantity, score, count_test)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)"; // Chen vao bang du lieu nhung cau hoi nguoi dung da luyen tap
        PreparedStatement stm2 = conn.prepareStatement(query);
        for(int k = 0; k < questionId.length; k++){
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String date = f.format(new Date());
            java.sql.Date sqlDate = new java.sql.Date(f.parse(date).getTime()); 
            stm2.setInt(1, getIdUser(u.getUsername()));
            stm2.setInt(2, questionId[k]);
            stm2.setString(3, "multiplechoice");
            stm2.setDate(4, sqlDate);
            stm2.setInt(5, 1);
            stm2.setDouble(6, u.getScore().get(k));
            stm2.setInt(7, u.getCountTest());
            stm2.execute(); 
        }
        stm.close();
        stm1.close();
        stm2.close();
        conn.close();
    }
    
    public void practiceInComplete(Scanner scanner, Level lv, User u) throws ClassNotFoundException, SQLException{
        Collections.shuffle(this.getInCompleteQ());
        List<Question> correctQ = new ArrayList<>();
        List<Question> inCorrectQ = new ArrayList<>();
        String[] notes = new String[100];
        String[] kws = new String[100];
        List<Question> questionsByLevel = new ArrayList<>();
        List<Double> score = new ArrayList<>();
        for(Question q : this.getInCompleteQ()){
            if(q.getLevel().getContent().toUpperCase().equals(lv.getContent().toUpperCase()))
                questionsByLevel.add(q); //Xu ly ngoai le else
        }
        for(int i = 0; i < 1; i++){
            System.out.println(questionsByLevel.get(i));
            InComplete icp = (InComplete) questionsByLevel.get(i);
            int j = 0;
            for(MultipleChoice c : icp.getQuestions()){
                System.out.print("(" + (j + 1) + "): " + c + "Answer: ");
                kws[j] = scanner.nextLine();
                for(int k = 0; k < c.getChoices().size(); k++){
                    if(c.getChoices().get(k).isCorrect())
                        notes[j] =  String.format("The correct answer is: %s\nNote: %s\n", c.getLABELS()[k], c.getChoices().get(k).getNote());
                }
                if(c.checkAnswer(kws[j])){
                    score.add(1.0);
                    correctQ.add(c);
                }
                else{
                    score.add(0.0);
                    inCorrectQ.add(c);
                }
                j++;
            }
        }
        //Neu so luong lam bai kiem tra = 0 thi tang len 1, nguoc lai tiep tuc tang tiep so luong lam bai kiem tra 1 don vi
        if(u.getNumberOfTests(Tester.getConnection()) != 0)
            u.setCountTest(u.getNumberOfTests(Tester.getConnection()) + 1);
        else
            u.setCountTest(1);
        u.setScore(score);
        
        for(int i = 0; i < 1; i++){
            System.out.println("\n========== RESULT ==========");
            System.out.println(questionsByLevel.get(i));
            InComplete icp = (InComplete) questionsByLevel.get(i);
            int j = 0;
            for(MultipleChoice c : icp.getQuestions()){
                System.out.printf("The correct answer for (%d):%sThe answer of user: %s\n%s\n",
                        (j+1), c, kws[j].toUpperCase(), notes[j]);
                j++;
            }
        }
    }
    
    /**
     * Phuong thuc luyen tap InComplete truy xuat tu CSDL
     * @param scanner
     * @param lv muc do nguoi dung muon luyen tap
     * @param u nguoi dung hien tai dang dang nhap
     * @param conn ket noi CSDL
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException 
     */
    public static void practiceInComplete(Scanner scanner, String lv, User u, Connection conn) throws ClassNotFoundException, SQLException, ParseException{
        Statement stm = conn.createStatement();
        Statement stm1 = conn.createStatement();
        Statement stm2 = conn.createStatement();
        
        ResultSet rs = stm.executeQuery("SELECT * FROM incomplete WHERE level = \"" + lv.toUpperCase() + "\" ORDER BY RAND() LIMIT 1");
        Level level = new Level(lv.toUpperCase());
        List<Integer> questionId = new ArrayList<>();
        QuestionManagement qs = new QuestionManagement();
        while(rs.next()){
            Category cateI = new Category("General");
            Question qI = new InComplete(rs.getString("content"), level, cateI);
            ResultSet rs1 = stm1.executeQuery("SELECT m.id, m.content as question, "
                        + "l.content as level, c.content as category "
                        + "FROM multiplechoice m "
                        + "INNER JOIN level l ON m.level_id = l.id "
                        + "INNER JOIN category c ON m.category_id = c.id "
                        + "WHERE m.incomplete_id = " + rs.getInt("id"));
            while(rs1.next()){
                questionId.add(rs1.getInt("id"));
                Level lvM = new Level(rs1.getString("level"));
                Category cateM = new Category(rs1.getString("category"));
                Question qM = new MultipleChoice(rs1.getString("question"), lvM, cateM);
                ResultSet rs2 = stm2.executeQuery("SELECT * FROM choice WHERE multiplechoice_id = " + rs1.getInt("id"));
                while(rs2.next()){
                    Choice ch = new Choice(rs2.getString("content"),
                            rs2.getBoolean("correct"), rs2.getString("note"));
                    qM.addChoice(ch);
                }
                qI.addQuestion(qM);
                qs.addQuestion(qI);
                rs2.close();
            }
            rs1.close();
        }
        qs.practiceInComplete(scanner, level, u);
        rs.close();
        
        String query = "INSERT INTO practice(user_id, question_id, type, date_practice, quantity, score, count_test)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stm3 = conn.prepareStatement(query);
        for(int i = 0; i < questionId.size(); i++){
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String date = f.format(new Date());
            java.sql.Date sqlDate = new java.sql.Date(f.parse(date).getTime()); 
            stm3.setInt(1, getIdUser(u.getUsername()));
            stm3.setInt(2, questionId.get(i));
            stm3.setString(3, "incomplete");
            stm3.setDate(4, sqlDate);
            stm3.setInt(5, 1);
            stm3.setDouble(6, u.getScore().get(i));
            stm3.setInt(7, u.getCountTest());
            stm3.execute(); 
        }
                                        
        stm.close();
        stm1.close();
        stm2.close();
        conn.close();
    }
    
    public void practiceConversation(Scanner scanner, Level lv, User u) throws ClassNotFoundException, SQLException{
        Collections.shuffle(this.getConversationQ());
        List<Question> correctQ = new ArrayList<>();
        List<Question> inCorrectQ = new ArrayList<>();
        String[] notes = new String[100];
        String[] kws = new String[100];
        List<Question> questionsByLevel = new ArrayList<>();
        List<Double> score = new ArrayList<>();
        for(Question q : this.getConversationQ()){
            if(q.getLevel().getContent().toUpperCase().equals(lv.getContent().toUpperCase()))
                questionsByLevel.add(q); //Xu ly ngoai le else
        }
        for(int i = 0; i < 1; i++){
            System.out.println(questionsByLevel.get(i));
            Conversation cvs = (Conversation) questionsByLevel.get(i);
            int j = 0;
            for(MultipleChoice c : cvs.getQuestions()){
                System.out.print("(" + (j + 1) + "): " + c + "Answer: ");
                kws[j] = scanner.nextLine();
                for(int k = 0; k < c.getChoices().size(); k++){
                    if(c.getChoices().get(k).isCorrect())
                        notes[j] =  String.format("The correct answer is: %s\nNote: %s\n", c.getLABELS()[k], c.getChoices().get(k).getNote());
                }
                if(c.checkAnswer(kws[j])){
                    score.add(1.0);
                    correctQ.add(c);
                }
                else{
                    score.add(0.0);
                    inCorrectQ.add(c);
                }
                j++;
            }
        }
        //Neu so luong lam bai kiem tra = 0 thi tang len 1, nguoc lai tiep tuc tang tiep so luong lam bai kiem tra 1 don vi
        if(u.getNumberOfTests(Tester.getConnection()) != 0)
            u.setCountTest(u.getNumberOfTests(Tester.getConnection()) + 1);
        else
            u.setCountTest(1);
        u.setScore(score);
        
        for(int i = 0; i < 1; i++){
            System.out.println("\n========== RESULT ==========");
            System.out.println(questionsByLevel.get(i));
            Conversation cvs = (Conversation) questionsByLevel.get(i);
            int j = 0;
            for(MultipleChoice c : cvs.getQuestions()){
                System.out.printf("The correct answer for (%d):%sThe answer of user: %s\n%s\n",
                        (j+1), c, kws[j].toUpperCase(), notes[j]);
                j++;
            }
        }
    }
    
    /**
     * Phuong thuc luyen tap Conversation truy xuat tu CSDL
     * @param scanner
     * @param lv muc do nguoi dung muon luyen tap
     * @param u nguoi dung hien tai dang dang nhap
     * @param conn ket noi CSDL
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException 
     */
    public static void practiceConversation(Scanner scanner, String lv, User u, Connection conn) throws ClassNotFoundException, SQLException, ParseException{
        Statement stm = conn.createStatement();
        Statement stm1 = conn.createStatement();
        Statement stm2 = conn.createStatement();
        
        ResultSet rs = stm.executeQuery("SELECT * FROM conversation WHERE level = \"" + lv.toUpperCase() + "\" ORDER BY RAND() LIMIT 1");
        Level level = new Level(lv.toUpperCase());
        int multipleID = 0;
        List<Integer> questionId = new ArrayList<>();
        QuestionManagement qs = new QuestionManagement();
        while(rs.next()){
            Category cateI = new Category("General");
            Question qC = new Conversation(rs.getString("content"), level, cateI);
            ResultSet rs1 = stm1.executeQuery("SELECT m.id, m.content as question, "
                        + "l.content as level, c.content as category "
                        + "FROM multiplechoice m "
                        + "INNER JOIN level l ON m.level_id = l.id "
                        + "INNER JOIN category c ON m.category_id = c.id "
                        + "WHERE m.conversation_id = " + rs.getInt("id"));
            while(rs1.next()){
                questionId.add(rs1.getInt("id"));
                Level lvM = new Level(rs1.getString("level"));
                Category cateM = new Category(rs1.getString("category"));
                Question qM = new MultipleChoice(rs1.getString("question"), lvM, cateM);
                multipleID = rs1.getInt("id");
                ResultSet rs2 = stm2.executeQuery("SELECT * FROM choice WHERE multiplechoice_id = " + multipleID);
                while(rs2.next()){
                    Choice ch = new Choice(rs2.getString("content"),
                            rs2.getBoolean("correct"), rs2.getString("note"));
                    qM.addChoice(ch);
                }
                qC.addQuestion(qM);
                qs.addQuestion(qC);
                rs2.close();
            }
            rs1.close();
        }
        qs.practiceConversation(scanner, level, u);
        rs.close();
        
        String query = "INSERT INTO practice(user_id, question_id, type, date_practice, quantity, score, count_test)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)"; // Chen vao bang du lieu nhung cau hoi nguoi dung da luyen tap
        PreparedStatement stm3 = conn.prepareStatement(query);
        for(int i = 0; i < questionId.size(); i++){
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String date = f.format(new Date());
            java.sql.Date sqlDate = new java.sql.Date(f.parse(date).getTime()); 
            stm3.setInt(1, getIdUser(u.getUsername()));
            stm3.setInt(2, questionId.get(i));
            stm3.setString(3, "conversation");
            stm3.setDate(4, sqlDate);
            stm3.setInt(5, 1);
            stm3.setDouble(6, u.getScore().get(i));
            stm3.setInt(7, u.getCountTest());
            stm3.execute(); 
        }
                                        
        stm.close();
        stm1.close();
        stm2.close();
        conn.close();
    }
    
    public List<Question> lookUpByContent(String c){
        List<Question> r = new ArrayList<>();
        for(Question q : this.questions){
            if(q.getContent().toUpperCase().contains(c.toUpperCase()))
                r.add(q);
        }
        return r;
    }
    
    public List<Question> lookUpByContent(String c, Connection conn) throws SQLException{
        List<Question> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT m.content as question, "
                + "l.content as level, c.content as category FROM multiplechoice m "
                + "INNER JOIN level l ON l.id = m.level_id "
                + "INNER JOIN category c ON c.id = m.category_id");
        this.questions = new ArrayList<>();
        while(rs.next()){
            Level l = new Level(rs.getString("level"));
            Category cate = new Category(rs.getString("category"));
            Question q = new MultipleChoice(rs.getString("question"), l, cate);
            this.questions.add(q);
        }
        r = this.lookUpByContent(c);
        rs.close();
        
        stm.close();
        conn.close();
        return r;
    }
    
    public List<Question> lookUpByCate(String c){
        List<Question> r = new ArrayList<>();
        for(Question q : this.questions){
            if(q.getCategory().toString().equals(c.toUpperCase()))
                r.add(q);
        }
        return r;
    }
    
    //Chi tim nhung cau hoi multiplechoice vi incomplete va conversation kh co danh muc
    public List<Question> lookUpByCate(String c, Connection conn) throws SQLException{
        List<Question> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT m.content as question, "
                + "l.content as level, c.content as category FROM multiplechoice m "
                + "INNER JOIN level l ON l.id = m.level_id "
                + "INNER JOIN category c ON c.id = m.category_id "
                + "WHERE m.incomplete_id IS NULL AND m.conversation_id IS NULL");
        this.questions = new ArrayList<>();
        while(rs.next()){
            Level l = new Level(rs.getString("level"));
            Category cate = new Category(rs.getString("category"));
            Question q = new MultipleChoice(rs.getString("question"), l, cate);
            this.questions.add(q);
        }
        r = this.lookUpByCate(c);
        rs.close();
        
        stm.close();
        conn.close();
        return r;
    }
    
    public List<Question> lookUpByLevel(String lv){
        List<Question> r = new ArrayList<>();
        for(Question q : this.questions){
            if(q.getLevel().getContent().equals(lv.toUpperCase()))
                r.add(q);
        }
        return r;
    }
    
    public List<Question> lookUpByLevel(String lv, Connection conn) throws SQLException{
        List<Question> r = new ArrayList<>();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT m.content as question, "
                + "l.content as level, c.content as category FROM multiplechoice m "
                + "INNER JOIN level l ON l.id = m.level_id "
                + "INNER JOIN category c ON c.id = m.category_id");
        this.questions = new ArrayList<>();
        while(rs.next()){
            Level l = new Level(rs.getString("level"));
            Category cate = new Category(rs.getString("category"));
            Question q = new MultipleChoice(rs.getString("question"), l, cate);
            this.questions.add(q);
        }
        r = this.lookUpByLevel(lv);
        rs.close();
        
        stm.close();
        conn.close();
        return r;
    }

    /**
     * @return the questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    
}
