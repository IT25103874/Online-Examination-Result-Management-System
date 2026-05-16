package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Question;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.QuestionPaper;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.QuestionPaperRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionPaperService {

    private final QuestionRepository questionRepository;
    private final QuestionPaperRepository paperRepository;

    public QuestionPaper generateAutoPaper(Integer subjectId, Integer count, String difficulty, String paperName, String instructions) {
        // 1. ලබාදුන් විෂයය (Subject ID) සහ අපහසුතා මට්ටම (Difficulty) අනුව ප්‍රශ්න දත්ත ගබඩාවෙන් ලබා ගැනීම
        List<Question> allQuestions = questionRepository.findBySubjectSubjectIdAndDifficultyLevel(subjectId, difficulty);

        // ---- Validation (ප්‍රශ්න පරීක්ෂා කිරීමේ කොටස) ----

        // 2. අදාළ නිර්ණායකයන්ට ගැලපෙන ප්‍රශ්න කිසිවක් නැතිනම් Paper එක සෑදීම නවත්වන්න
        if (allQuestions == null || allQuestions.isEmpty()) {
            throw new RuntimeException("ප්‍රශ්න පත්‍රය සෑදිය නොහැක! මෙම විෂයට සහ අපහසුතා මට්ටමට අදාළ ප්‍රශ්න කිසිවක් දත්ත පද්ධතියේ නොමැත.");
        }

        // 3. ඔබ ඉල්ලා සිටින ප්‍රශ්න ප්‍රමාණය (count) දත්ත පද්ධතියේ ඇති ප්‍රශ්න ප්‍රමාණයට වඩා වැඩි නම්
        if (allQuestions.size() < count) {
            throw new RuntimeException("ප්‍රමාණවත් ප්‍රශ්න සංඛ්‍යාවක් නැත! ඔබ ප්‍රශ්න " + count + " ක් ඉල්ලා සිටියද, පද්ධතියේ පවතින්නේ ප්‍රශ්න " + allQuestions.size() + " ක් පමණි.");
        }

        // --------------------------------------------------

        // 4. ප්‍රශ්න අහඹු ලෙස මාරු කිරීම (Shuffle)
        Collections.shuffle(allQuestions);

        // 5. අවශ්‍ය ප්‍රමාණය (Count) පමණක් වෙන් කර ලබා ගැනීම
        List<Question> selected = allQuestions.stream().limit(count).collect(Collectors.toList());

        // 6. ප්‍රශ්න සහිතව ප්‍රශ්න පත්‍රය (Question Paper) සේව් කිරීම
        QuestionPaper paper = new QuestionPaper();
        paper.setPaperName(paperName);
        paper.setInstructions(instructions);
        paper.setQuestions(selected); // මෙහිදී හිස් නොවන ප්‍රශ්න ලැයිස්තුවක් අනිවාර්යයෙන්ම එකතු වේ.

        return paperRepository.save(paper);
    }

    public QuestionPaper getPaperById(int id) {
        // නිවැරදි කර ඇති repository reference එක
        return paperRepository.findById(id).orElse(null);
    }
}