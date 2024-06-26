package GestionRecursos.Model.Dispositivo.DAO;

import Comunication.ConnBD.DataBaseConnection;
import GestionRecursos.Model.DAO;
import GestionRecursos.Model.Dispositivo.Ent.Dispositivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class  DispositivoDAO implements DAO<Dispositivo> {

    @Override
    public void insert(Dispositivo dispositivo) {

        String sql = "INSERT INTO dispositivos (topic, estado) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, dispositivo.getTopic());
            stmt.setInt(2, dispositivo.getEstado());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public void update(Dispositivo dispositivo) {

        String sql = "UPDATE dispositivos SET topic = ?, estado = ? WHERE iddispositivos = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, dispositivo.getTopic());
            stmt.setInt(2, dispositivo.getEstado());
            stmt.setInt(3, dispositivo.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM dispositivos WHERE iddispositivos = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

    }

    @Override
    public Dispositivo get(int id) {

        Dispositivo dispositivo = null;
        String sql = "SELECT * FROM dispositivos WHERE iddispositivos = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dispositivo = crearDispositivo(rs);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivo;

    }

    @Override
    public Dispositivo getBy(String topic) {
        return null;
    }

    public Dispositivo getByTopic(String topic) {

        Dispositivo dispositivo = null;
        String sql = "SELECT * FROM dispositivos WHERE topic = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, topic);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dispositivo = crearDispositivo(rs);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivo;

    }

    public List<Dispositivo> getAllByCultivo(String topic) {

        List<Dispositivo> dispositivos = new ArrayList<>();
        String sql = "SELECT d2.* " +
                "FROM dispositivos d1 " +
                "JOIN cultivos_dispositivos cd1 ON d1.iddispositivos = cd1.iddispositivos " +
                "JOIN cultivos c ON cd1.idcultivos = c.idcultivos JOIN cultivos_dispositivos cd2 ON c.idcultivos = cd2.idcultivos " +
                "JOIN dispositivos d2 ON cd2.iddispositivos = d2.iddispositivos " +
                "WHERE d1.topic = ?;";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, topic);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dispositivo dispositivo = crearDispositivo(rs);
                    dispositivos.add(dispositivo);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivos;

    }

    public List<Dispositivo> getAllByCultivoID(int id) {

        List<Dispositivo> dispositivos = new ArrayList<>();
        String sql = "SELECT d.iddispositivos, d.topic, d.estado " +
                "FROM dispositivos d " +
                "JOIN cultivos_dispositivos cd " +
                "ON d.iddispositivos = cd.iddispositivos " +
                "WHERE cd.idcultivos = ?;";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dispositivo dispositivo = crearDispositivo(rs);
                    dispositivos.add(dispositivo);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivos;

    }


    public List<Dispositivo> getAll() {

        List<Dispositivo> dispositivos = new ArrayList<>();
        String sql = "SELECT * FROM dispositivos";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                Dispositivo dispositivo = crearDispositivo(rs);
                dispositivos.add(dispositivo);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivos;

    }

    private Dispositivo crearDispositivo(ResultSet rs) throws SQLException {

        return new Dispositivo(rs.getInt(1), rs.getString(2), rs.getInt(3));

    }

    public int estadoBomba(String topic){

        return getByTopic(topic).getEstado();

    }

    public void cambiarEstadoBomba(String topic){

        if (topic.contains("actuator/waterpump")) {

            int estado = (estadoBomba(topic) == 0) ? 1 : 0;
            String sql = "UPDATE dispositivos SET estado = ? WHERE topic = ?;";

            try (Connection conn = DataBaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, estado);
                stmt.setString(2, topic);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }

        }

    }


}


