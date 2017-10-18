package de.djuelg.neuronizer.storage;

import com.fernandocejas.arrow.optional.Optional;

import de.djuelg.neuronizer.domain.model.preview.Note;
import de.djuelg.neuronizer.domain.repository.NoteRepository;
import de.djuelg.neuronizer.storage.converter.NoteDAOConverter;
import de.djuelg.neuronizer.storage.converter.RealmConverter;
import de.djuelg.neuronizer.storage.model.NoteDAO;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static de.djuelg.neuronizer.storage.RepositoryManager.createConfiguration;

/**
 * Created by dmilicic on 1/29/16.
 */
public class NoteRepositoryImpl implements NoteRepository {

    private final RealmConfiguration configuration;

    public NoteRepositoryImpl(String realmName) {
        this.configuration = createConfiguration(realmName);
    }

    // RealmConfiguration injectable for testing
    NoteRepositoryImpl(RealmConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<Note> getNoteById(String uuid) {
        Realm realm = Realm.getInstance(configuration);
        Optional<NoteDAO> noteDAO = Optional.fromNullable(realm.where(NoteDAO.class).equalTo("uuid", uuid).findFirst());
        Optional<Note> note = noteDAO.transform(new NoteDAOConverter());
        realm.close();
        return note;

    }

    @Override
    public void update(Note updatedNote) {
        Realm realm = Realm.getInstance(configuration);

        final NoteDAO noteDAO = RealmConverter.convert(updatedNote);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(noteDAO);
            }
        });
        realm.close();
    }
}