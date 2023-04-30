package onetoone.Lobbies;

import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;

    @Autowired
    public LobbyService(LobbyRepository lobbyRepository, UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

    public Lobby createLobby(User host, LobbyDto lobbyDto) {
        Lobby lobby = new Lobby();
        lobby.setName(lobbyDto.getName());
        lobby.setMaxMembers(lobbyDto.getMaxMembers());
        lobby.setPrivate(lobbyDto.isPrivate());
        lobby.setAccessCode(lobbyDto.getAccessCode());
        lobby.setHost(host);
        lobby.getMembers().add(host);

        return lobbyRepository.save(lobby);
    }

    public List<Lobby> getAvailableLobbies() {
        List<Lobby> availableLobbies = new ArrayList<>();
        for (Lobby lobby : lobbyRepository.findAll()) {
            if (!lobby.isPrivate() && lobby.getMembers().size() < lobby.getMaxMembers()) {
                availableLobbies.add(lobby);
            }
        }
        return availableLobbies;
    }

    public List<LobbyListDto> getLobbyList() {
        List<Lobby> lobbies = lobbyRepository.findAll();
        List<LobbyListDto> lobbyList = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            LobbyListDto lobbyListDto = new LobbyListDto();
            lobbyListDto.setId(lobby.getId());
            lobbyListDto.setName(lobby.getName());
            lobbyListDto.setCurrentMembers(lobby.getMembers().size());
            lobbyListDto.setMaxMembers(lobby.getMaxMembers());
            lobbyListDto.setPrivate(lobby.isPrivate());
            lobbyListDto.setFull(lobby.getMembers().size() >= lobby.getMaxMembers());
            lobbyList.add(lobbyListDto);
        }

        return lobbyList;
    }

    public Optional<Lobby> joinLobby(User user, int lobbyId, String accessCode) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);

        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            if (lobby.getMembers().size() < lobby.getMaxMembers() &&
                    ((!lobby.isPrivate()) || (lobby.isPrivate() && lobby.getAccessCode().equals(accessCode)))) {
                lobby.getMembers().add(user);
                lobbyRepository.save(lobby);
                return Optional.of(lobby);
            }
        }

        return Optional.empty();
    }

    public Lobby updateLobby(User host, Lobby lobby, LobbyDto lobbyDto) {
        if (host.equals(lobby.getHost())) {
            lobby.setName(lobbyDto.getName());
            lobby.setMaxMembers(lobbyDto.getMaxMembers());
            lobby.setPrivate(lobbyDto.isPrivate());
            lobby.setAccessCode(lobbyDto.getAccessCode());
            return lobbyRepository.save(lobby);
        }
        return null;
    }

    public boolean leaveLobby(User user, Lobby lobby) {
        if (lobby.getMembers().contains(user)) {
            if (lobby.getHost().equals(user)) {
                lobbyRepository.delete(lobby);
                return true;
            } else {
                lobby.getMembers().remove(user);
                lobbyRepository.save(lobby);
                return true;
            }
        }
        return false;
    }
}