package com.docify.docify_be.file.service;

import com.docify.docify_be.common.exception.DocifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidationServiceTest {

    private FileValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new FileValidationService();
        ReflectionTestUtils.setField(validationService, "maxFileSizeMb", 50L);
    }

    @Test
    void acceptsSupportedMarkdownConversion() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.md",
                "text/markdown",
                "# Hello".getBytes()
        );

        assertThatCode(() -> validationService.validateForConversion(file, "md", "pdf"))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsUnsupportedPdfConversionForMvp() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "%PDF-1.7".getBytes()
        );

        assertThatThrownBy(() -> validationService.validateForConversion(file, "pdf", "docx"))
                .isInstanceOf(DocifyException.class)
                .hasMessageContaining("Unsupported source file type");
    }

    @Test
    void rejectsInvalidOfficeMagicNumber() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "not-a-zip".getBytes()
        );

        assertThatThrownBy(() -> validationService.validateForConversion(file, "docx", "pdf"))
                .isInstanceOf(DocifyException.class)
                .hasMessageContaining("valid DOCX/PPTX archives");
    }
}
