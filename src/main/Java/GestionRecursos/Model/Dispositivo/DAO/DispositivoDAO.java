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

    public Dispositivo getSensorsByCultivo (int idCultivo) {
        Dispositivo dispositivo = null;
        String sql = "SELECT d.iddispositivos, d.topic, d.estado, cd.idcultivos " +
                "FROM dispositivos d JOIN cultivos_dispositivos cd ON d.iddispositivos = cd.iddispositivos " +
                "WHERE cd.idcultivos = ? AND d.topic LIKE \"%sensor%\";";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idCultivo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dispositivo = crearDispositivo(rs);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return dispositivo;
    }

    public Dispositivo getBombaByCultivo (int id) {
        Dispositivo dispositivo = null;
        String sql = "SELECT d.iddispositivos, d.topic, d.estado, cd.idcultivos " +
                "FROM dispositivos d JOIN cultivos_dispositivos cd ON d.iddispositivos = cd.iddispositivos " +
                "WHERE cd.idcultivos = ? AND d.topic LIKE \"%actuator%\"" +
                "LIMIT 1;";
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

