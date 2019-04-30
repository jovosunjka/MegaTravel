package com.bsep_sbz.PKI.service.certificate;

import com.bsep_sbz.PKI.model.IssuerData;
import com.bsep_sbz.PKI.model.SubjectData;

import java.security.cert.X509Certificate;

public interface CertificateGeneratorService {

    X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData);
}
