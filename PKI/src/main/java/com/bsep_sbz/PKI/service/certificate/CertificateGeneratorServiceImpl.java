package com.bsep_sbz.PKI.service.certificate;


import com.bsep_sbz.PKI.model.IssuerData;
import com.bsep_sbz.PKI.model.SubjectData;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateGeneratorServiceImpl implements CertificateGeneratorService {
	public CertificateGeneratorServiceImpl() {}

	@Override
	public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			//Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
			//Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
			//Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			//Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
			builder = builder.setProvider("BC");

			//Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
			ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

			//Postavljaju se podaci za generisanje sertifiakta
			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					new BigInteger(subjectData.getSerialNumber()),
					subjectData.getStartDate(),
					subjectData.getEndDate(),
					subjectData.getX500name(),
					subjectData.getPublicKey());

			// JOVO DODAO, NIJE BILO U LUBURINOM KODU
			//----------------------------------------------------------------------------------------------------------
			// Na ovom linku je objasnjeno zasto treba koristiti subjectAlternativeNames:
			// https://support.google.com/chrome/a/answer/7391219?hl=en
			final List<ASN1Encodable> subjectAlternativeNames = new ArrayList<ASN1Encodable>();
			subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, "localhost"));
			// primeri sa neta
			//subjectAlternativeNames.add(new GeneralName(GeneralName.iPAddress, publicIPAddress));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.iPAddress, "10.0.0.1"));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.iPAddress, "10.1.1.1"));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, "kubernetes"));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, "kubernetes.default"));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, "kubernetes.default.svc"));
			//subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, "kubernetes.default.svc.cluster.local"));
			final DERSequence subjectAlternativeNamesExtension = new DERSequence(
					subjectAlternativeNames.toArray(new ASN1Encodable[subjectAlternativeNames.size()]));
			//zastarelo
			//certGen.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAlternativeNamesExtension);
			certGen.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNamesExtension);
			//----------------------------------------------------------------------------------------------------------

			//Generise se sertifikat
			X509CertificateHolder certHolder = certGen.build(contentSigner);

			//Builder generise sertifikat kao objekat klase X509CertificateHolder
			//Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
			JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			certConverter = certConverter.setProvider("BC");

			//Konvertuje objekat u sertifikat
			return certConverter.getCertificate(certHolder);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (OperatorCreationException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (CertIOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
