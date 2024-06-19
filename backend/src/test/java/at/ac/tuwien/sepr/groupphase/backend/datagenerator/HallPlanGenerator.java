package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.config.DataGenerationConfig;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallPlan;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSector;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSpot;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallPlanRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.HallSpotRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Order(1)
@Profile("generateData")
@Component
public class HallPlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(HallPlanGenerator.class);
    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private HallSectorRepository hallSectorRepository;

    @Autowired
    private HallSpotRepository hallSpotRepository;

    @Autowired
    private DataGenerationConfig dataGenerationConfig;

    private static final double SEAT_RADIUS = 5.0;
    private static final double SEAT_DISTANCE = 15.0;
    private static final int MAX_WIDTH = 1000;
    private static final int MAX_HEIGHT = 1000;
    private static final int MAX_SECTORS = 5; // Number of sectors to generate

    @PostConstruct
    private void generateData() throws ForbiddenException, ValidationException {
        generateRandomHallPlans(dataGenerationConfig.hallPlanAmount); // Generate 300 random hall plans
    }

    private void generateRandomHallPlans(Long numberOfHallPlans) throws ForbiddenException, ValidationException {
        for (int i = 0; i < numberOfHallPlans; i++) {
            log.info("Generating hall plan " + (i + 1) + "/" + numberOfHallPlans);
            var hallPlan = new HallPlan(
                loadImage("backend/src/test/resources/datagen/hallplan/openair.txt"),
                "Random Hall Plan " + (i + 1),
                List.of()
            );
            hallPlanRepository.saveAndFlush(hallPlan);

            List<HallSector> sectors = generateRandomSectors(hallPlan, MAX_SECTORS);
            for (HallSector sector : sectors) {
                hallSectorRepository.saveAndFlush(sector);

                var seats = calculateSeats(sector, parsePolygon(sector.getFrontendCoordinates()));
                hallSpotRepository.saveAllAndFlush(seats);
            }
        }
    }

    private List<HallSector> generateRandomSectors(HallPlan hallPlan, int maxSectors) {
        List<HallSector> sectors = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < maxSectors; i++) {
            List<Point2D.Double> polygon;
            boolean isValidPolygon;
            do {
                polygon = generateRandomPolygon(random);
                isValidPolygon = !isIntersectingWithExistingSectors(polygon, sectors);
            } while (!isValidPolygon);

            HallSector sector = new HallSector(hallPlan, "Sector " + (i + 1), polygonToString(polygon), List.of());
            sector.setColor(String.format("#%06X", random.nextInt(0xFFFFFF)));
            sectors.add(sector);
        }
        return sectors;
    }

    private List<Point2D.Double> generateRandomPolygon(Random random) {
        List<Point2D.Double> polygon = new ArrayList<>();
        int numberOfPoints = random.nextInt(3) + 3; // Polygons with 3 to 5 points

        for (int i = 0; i < numberOfPoints; i++) {
            double x = random.nextDouble() * MAX_WIDTH;
            double y = random.nextDouble() * MAX_HEIGHT;
            polygon.add(new Point2D.Double(x, y));
        }
        return polygon;
    }

    private boolean isIntersectingWithExistingSectors(List<Point2D.Double> newPolygon, List<HallSector> existingSectors) {
        Path2D newPath = createPathFromPolygon(newPolygon);
        for (HallSector sector : existingSectors) {
            Path2D existingPath = createPathFromPolygon(parsePolygon(sector.getFrontendCoordinates()));
            if (newPath.intersects(existingPath.getBounds2D())) {
                return true;
            }
        }
        return false;
    }

    private Path2D createPathFromPolygon(List<Point2D.Double> polygon) {
        Path2D path = new Path2D.Double();
        if (polygon.size() > 0) {
            path.moveTo(polygon.get(0).x, polygon.get(0).y);
            for (int i = 1; i < polygon.size(); i++) {
                path.lineTo(polygon.get(i).x, polygon.get(i).y);
            }
            path.closePath();
        }
        return path;
    }

    private List<Point2D.Double> parsePolygon(String coordinates) {
        List<Point2D.Double> polygon = new ArrayList<>();
        String[] points = coordinates.replace("[", "").replace("]", "").split("},\\{");
        for (String point : points) {
            String[] coords = point.replace("{", "").replace("}", "").replace("\"", "").split(",");
            double x = Double.parseDouble(coords[0].split(":")[1]);
            double y = Double.parseDouble(coords[1].split(":")[1]);
            polygon.add(new Point2D.Double(x, y));
        }
        return polygon;
    }

    private List<HallSeat> calculateSeats(HallSector sector, List<Point2D.Double> polygon) {
        List<HallSeat> seats = new ArrayList<>();
        double minX = polygon.stream().mapToDouble(Point2D.Double::getX).min().orElse(0);
        double maxX = polygon.stream().mapToDouble(Point2D.Double::getX).max().orElse(0);
        double minY = polygon.stream().mapToDouble(Point2D.Double::getY).min().orElse(0);
        double maxY = polygon.stream().mapToDouble(Point2D.Double::getY).max().orElse(0);

        for (double y = minY + SEAT_RADIUS; y <= maxY - SEAT_RADIUS; y += SEAT_DISTANCE) {
            for (double x = minX + SEAT_RADIUS; x <= maxX - SEAT_RADIUS; x += SEAT_DISTANCE) {
                Point2D.Double point = new Point2D.Double(x, y);
                if (isPointInPolygon(point, polygon)) {
                    seats.add(new HallSeat(sector, pointToString(point)));
                }
            }
        }
        return seats;
    }

    private boolean isPointInPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
        int intersections = 0;
        for (int i = 0; i < polygon.size(); i++) {
            Point2D.Double p1 = polygon.get(i);
            Point2D.Double p2 = polygon.get((i + 1) % polygon.size());

            if (((p1.getY() > point.getY()) != (p2.getY() > point.getY())) &&
                (point.getX() < (p2.getX() - p1.getX()) * (point.getY() - p1.getY()) / (p2.getY() - p1.getY()) + p1.getX())) {
                intersections++;
            }
        }
        return (intersections % 2) != 0;
    }

    private String polygonToString(List<Point2D.Double> polygon) {
        StringBuilder sb = new StringBuilder("[");
        for (Point2D.Double point : polygon) {
            sb.append("{\"x\":").append(point.getX()).append(",\"y\":").append(point.getY()).append("},");
        }
        sb.setLength(sb.length() - 1);  // Remove the trailing comma
        sb.append("]");
        return sb.toString();
    }

    private String pointToString(Point2D.Double point) {
        return "{\"x\":" + point.getX() + ",\"y\":" + point.getY() + "}";
    }

    private String loadImage(String path) {
        String content = "error while loading hallplan image";
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
