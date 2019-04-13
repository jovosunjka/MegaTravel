package com.bsep.PKI.service.certificate;

import com.bsep.PKI.model.IssuerData;
import com.bsep.PKI.model.SubjectData;

import java.security.cert.X509Certificate;

public interface CertificateGeneratorService {

    X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData);
}
