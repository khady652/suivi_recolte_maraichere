package sn.agriculture.culture_service.service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.agriculture.culture_service.dtos.response.HistoriqueCultureResponse;
import sn.agriculture.culture_service.dtos.response.HistoriqueCultureResponse;
import sn.agriculture.culture_service.dtos.response.RapportAgricoleResponse;

import java.io.ByteArrayOutputStream;
import java.util.Map;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class PdfRapportService {

        // Couleurs
        private static final BaseColor VERT_AGRI =
                new BaseColor(46, 125, 50);
        private static final BaseColor VERT_CLAIR =
                new BaseColor(200, 230, 201);
        private static final BaseColor GRIS_CLAIR =
                new BaseColor(245, 245, 245);

        // Polices
        private static final Font TITRE_FONT =
                new Font(Font.FontFamily.HELVETICA, 20,
                        Font.BOLD, BaseColor.WHITE);
        private static final Font SECTION_FONT =
                new Font(Font.FontFamily.HELVETICA, 13,
                        Font.BOLD, VERT_AGRI);
        private static final Font HEADER_FONT =
                new Font(Font.FontFamily.HELVETICA, 10,
                        Font.BOLD, BaseColor.WHITE);
        private static final Font NORMAL_FONT =
                new Font(Font.FontFamily.HELVETICA, 10,
                        Font.NORMAL, BaseColor.BLACK);
        private static final Font BOLD_FONT =
                new Font(Font.FontFamily.HELVETICA, 10,
                        Font.BOLD, BaseColor.BLACK);

        public byte[] genererPdf(RapportAgricoleResponse rapport) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Document document = new Document(PageSize.A4, 40, 40, 60, 40);
                PdfWriter.getInstance(document, baos);
                document.open();

                // ── EN-TÊTE ───────────────────────────────────
                ajouterEnTete(document, rapport);

                // ── INFORMATIONS GÉNÉRALES ────────────────────
                ajouterInfosGenerales(document, rapport);

                // ── SUPERFICIE ────────────────────────────────
                ajouterSuperficie(document, rapport);

                // ── PRODUCTIONS ───────────────────────────────
                ajouterProductions(document, rapport);

                // ── ALERTES ───────────────────────────────────
                ajouterAlertes(document, rapport);

                // ── HISTORIQUE ────────────────────────────────
                ajouterHistorique(document, rapport);

                document.close();
                return baos.toByteArray();

            } catch (Exception e) {
                log.error("Erreur génération PDF : {}", e.getMessage());
                throw new RuntimeException("Erreur génération PDF !");
            }
        }

        // ── EN-TÊTE ───────────────────────────────────────────
        private void ajouterEnTete(Document doc,
                                   RapportAgricoleResponse r) throws DocumentException {

            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(VERT_AGRI);
            cell.setPadding(20);
            cell.setBorder(Rectangle.NO_BORDER);

            Paragraph titre = new Paragraph(
                    "RAPPORT AGRICOLE ANNUEL " + r.getAnnee(),
                    TITRE_FONT);
            titre.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(titre);

            Paragraph sousTitre = new Paragraph(
                    r.getTypeTerritoire() + " DE " +
                            r.getTerritoire().toUpperCase(),
                    new Font(Font.FontFamily.HELVETICA, 13,
                            Font.NORMAL, BaseColor.WHITE));
            sousTitre.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(sousTitre);

            header.addCell(cell);
            doc.add(header);
            doc.add(Chunk.NEWLINE);
        }

        // ── INFORMATIONS GÉNÉRALES ────────────────────────────
        private void ajouterInfosGenerales(Document doc,
                                           RapportAgricoleResponse r) throws DocumentException {

            ajouterTitreSection(doc, "INFORMATIONS GÉNÉRALES");

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);

            ajouterLigne(table, "Directeur",
                    r.getPrenomDirecteur() + " " + r.getNomDirecteur());
            ajouterLigne(table, "Territoire", r.getTerritoire());
            ajouterLigne(table, "Type", r.getTypeTerritoire());
            ajouterLigne(table, "Année", String.valueOf(r.getAnnee()));
            ajouterLigne(table, "Date de génération",
                    r.getDateGeneration().toString());

            doc.add(table);
            doc.add(Chunk.NEWLINE);
        }

        // ── SUPERFICIE ────────────────────────────────────────
        private void ajouterSuperficie(Document doc,
                                       RapportAgricoleResponse r) throws DocumentException {

            ajouterTitreSection(doc, "SUPERFICIE");

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);

            ajouterLigne(table, "Superficie totale",
                    r.getSuperficieTotale() + " m²");
            ajouterLigne(table, "Surface cultivée",
                    r.getSurfaceCultivee() + " m²");
            ajouterLigne(table, "Taux d'occupation",
                    r.getTauxOccupation() + "%");

            doc.add(table);
            doc.add(Chunk.NEWLINE);
        }

        // ── PRODUCTIONS ───────────────────────────────────────
        private void ajouterProductions(Document doc,
                                        RapportAgricoleResponse r) throws DocumentException {

            ajouterTitreSection(doc, "PRODUCTIONS");

            // Résumé
            PdfPTable resume = new PdfPTable(2);
            resume.setWidthPercentage(100);
            resume.setSpacingBefore(5);

            ajouterLigne(resume, "Total produit",
                    r.getTotalProduitKg() + " kg");
            ajouterLigne(resume, "Total prévu",
                    r.getTotalPrevuKg() + " kg");
            ajouterLigne(resume, "Taux de réalisation",
                    r.getTauxRealisation());
            ajouterLigne(resume, "Nombre de récoltes",
                    String.valueOf(r.getNombreRecoltes()));

            doc.add(resume);
            doc.add(Chunk.NEWLINE);

            // Par culture
            if (r.getProductionParCulture() != null
                    && !r.getProductionParCulture().isEmpty()) {
                ajouterSousSection(doc, "Production par culture");
                doc.add(creerTableauMap(
                        r.getProductionParCulture(), "Culture", "Quantité (kg)"));
                doc.add(Chunk.NEWLINE);
            }

            // Par variété
            if (r.getProductionParVariete() != null
                    && !r.getProductionParVariete().isEmpty()) {
                ajouterSousSection(doc, "Production par variété");
                doc.add(creerTableauMap(
                        r.getProductionParVariete(), "Variété", "Quantité (kg)"));
                doc.add(Chunk.NEWLINE);
            }

            // Par saison
            if (r.getProductionParSaison() != null
                    && !r.getProductionParSaison().isEmpty()) {
                ajouterSousSection(doc, "Production par saison");
                doc.add(creerTableauMap(
                        r.getProductionParSaison(), "Saison", "Quantité (kg)"));
                doc.add(Chunk.NEWLINE);
            }
        }

        // ── ALERTES ───────────────────────────────────────────
        private void ajouterAlertes(Document doc,
                                    RapportAgricoleResponse r) throws DocumentException {

            ajouterTitreSection(doc, "ALERTES");

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);

            // Couleur rouge si alerte
            BaseColor couleurRetard = r.getCulturesEnRetard() > 0
                    ? new BaseColor(255, 205, 210)
                    : VERT_CLAIR;
            BaseColor couleurSeuil = r.getRecoltesSousSeuil() > 0
                    ? new BaseColor(255, 205, 210)
                    : VERT_CLAIR;

            ajouterLigneColoree(table,
                    "Cultures en retard de récolte",
                    String.valueOf(r.getCulturesEnRetard()),
                    couleurRetard);
            ajouterLigneColoree(table,
                    "Récoltes sous le seuil (< 70%)",
                    String.valueOf(r.getRecoltesSousSeuil()),
                    couleurSeuil);

            doc.add(table);
            doc.add(Chunk.NEWLINE);
        }

        // ── HISTORIQUE ────────────────────────────────────────
        private void ajouterHistorique(Document doc,
                                       RapportAgricoleResponse r) throws DocumentException {

            ajouterTitreSection(doc, "ÉVOLUTION ANNUELLE");

            // Historique surface
            if (r.getHistoriqueSurface() != null
                    && !r.getHistoriqueSurface().isEmpty()) {
                ajouterSousSection(doc, "Surface cultivée par année");

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);

                // Header
                ajouterHeaderTableau(table,
                        new String[]{"Année", "Surface (m²)"});

                // Données
                boolean alterner = false;
                for (HistoriqueCultureResponse h :
                        r.getHistoriqueSurface()) {
                    BaseColor bg = alterner
                            ? GRIS_CLAIR : BaseColor.WHITE;
                    ajouterLigneColoree(table,
                            String.valueOf(h.getAnnee()),
                            h.getSurfaceCultivee() + " m²", bg);
                    alterner = !alterner;
                }
                doc.add(table);
                doc.add(Chunk.NEWLINE);
            }

            // Historique production
            if (r.getHistoriqueProduction() != null
                    && !r.getHistoriqueProduction().isEmpty()) {
                ajouterSousSection(doc, "Production par année");
                doc.add(creerTableauMap(
                        r.getHistoriqueProduction(),
                        "Année", "Production (kg)"));
            }
        }

        // ── MÉTHODES UTILITAIRES ──────────────────────────────

        private void ajouterTitreSection(Document doc, String titre)
                throws DocumentException {
            Paragraph p = new Paragraph(titre, SECTION_FONT);
            p.setSpacingBefore(10);
            p.setSpacingAfter(5);
            doc.add(p);

            // Ligne séparatrice
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(VERT_AGRI);
            cell.setFixedHeight(2);
            cell.setBorder(Rectangle.NO_BORDER);
            line.addCell(cell);
            doc.add(line);
        }

        private void ajouterSousSection(Document doc, String titre)
                throws DocumentException {
            Paragraph p = new Paragraph(titre, BOLD_FONT);
            p.setSpacingBefore(5);
            doc.add(p);
        }

        private void ajouterLigne(PdfPTable table,
                                  String cle, String valeur) {
            PdfPCell cellCle = new PdfPCell(
                    new Phrase(cle, BOLD_FONT));
            cellCle.setBackgroundColor(GRIS_CLAIR);
            cellCle.setPadding(6);
            table.addCell(cellCle);

            PdfPCell cellVal = new PdfPCell(
                    new Phrase(valeur != null ? valeur : "-",
                            NORMAL_FONT));
            cellVal.setPadding(6);
            table.addCell(cellVal);
        }

        private void ajouterLigneColoree(PdfPTable table,
                                         String cle, String valeur, BaseColor couleur) {
            PdfPCell cellCle = new PdfPCell(
                    new Phrase(cle, BOLD_FONT));
            cellCle.setBackgroundColor(couleur);
            cellCle.setPadding(6);
            table.addCell(cellCle);

            PdfPCell cellVal = new PdfPCell(
                    new Phrase(valeur != null ? valeur : "-",
                            NORMAL_FONT));
            cellVal.setBackgroundColor(couleur);
            cellVal.setPadding(6);
            table.addCell(cellVal);
        }

        private void ajouterHeaderTableau(PdfPTable table,
                                          String[] headers) {
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(h, HEADER_FONT));
                cell.setBackgroundColor(VERT_AGRI);
                cell.setPadding(8);
                table.addCell(cell);
            }
        }

        private PdfPTable creerTableauMap(Map<String, Double> data,
                                          String colCle, String colVal)
                throws DocumentException {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);

            ajouterHeaderTableau(table,
                    new String[]{colCle, colVal});

            boolean alterner = false;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                BaseColor bg = alterner
                        ? GRIS_CLAIR : BaseColor.WHITE;
                ajouterLigneColoree(table,
                        entry.getKey(),
                        entry.getValue() + " kg", bg);
                alterner = !alterner;
            }
            return table;
        }
    }

