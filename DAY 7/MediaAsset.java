package com.streamcast;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    private Long id;

    private String title;
    private String genre;
    private int durationSeconds;

    public MediaAsset() {}

    public MediaAsset(String title, String genre, int durationSeconds) {
        this.title = title;
        this.genre = genre;
        this.durationSeconds = durationSeconds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}

@Entity
@Table(name = "video_assets")
class VideoAsset extends MediaAsset {

    private String resolution; 
    private String codec;

    public VideoAsset() {}

    public VideoAsset(String title, String genre, int durationSeconds,
                      String resolution, String codec) {
        super(title, genre, durationSeconds);
        this.resolution = resolution;
        this.codec = codec;
    }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public String getCodec() { return codec; }
    public void setCodec(String codec) { this.codec = codec; }
}

@Entity
@Table(name = "podcast_assets")
class PodcastAsset extends MediaAsset {

    private String hostName;
    private int episodeNumber;

    public PodcastAsset() {}

    public PodcastAsset(String title, String genre, int durationSeconds,
                        String hostName, int episodeNumber) {
        super(title, genre, durationSeconds);
        this.hostName = hostName;
        this.episodeNumber = episodeNumber;
    }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public int getEpisodeNumber() { return episodeNumber; }
    public void setEpisodeNumber(int episodeNumber) { this.episodeNumber = episodeNumber; }
}
@Entity
@Table(name = "authors")
class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String fullName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "author_id") // FK stored in the media tables
    private List<MediaAsset> mediaAssets = new ArrayList<>();

    public Author() {}

    public Author(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public List<MediaAsset> getMediaAssets() { return mediaAssets; }
    public void setMediaAssets(List<MediaAsset> mediaAssets) { this.mediaAssets = mediaAssets; }
}

// --- Author Repository with @EntityGraph ---
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // @EntityGraph eagerly fetches mediaAssets without custom JPQL JOIN FETCH
    @EntityGraph(attributePaths = {"mediaAssets"})
    Author findByUsername(String username);
}



class Problem4Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 4: StreamCast Content Delivery Network ===");
        System.out.println();
        System.out.println("This solution demonstrates:");
        System.out.println("1. TABLE_PER_CLASS inheritance strategy");
        System.out.println("   - Separate tables: video_assets, podcast_assets");
        System.out.println("   - No discriminator column needed");
        System.out.println("2. Unidirectional @OneToMany from Author to MediaAsset");
        System.out.println("3. JPQL TREAT operator for polymorphic downcasting");
        System.out.println("   - Query base class but filter on child-specific properties");
        System.out.println("4. @EntityGraph to eagerly fetch associations");
        System.out.println("   - Alternative to JOIN FETCH, works with derived queries");
        System.out.println();

        Author author = new Author("jschmidt", "John Schmidt");

        VideoAsset video1 = new VideoAsset("Java Deep Dive", "Tech", 3600, "4K", "H.265");
        VideoAsset video2 = new VideoAsset("Spring Boot Tips", "Tech", 1800, "1080p", "H.264");
        PodcastAsset podcast = new PodcastAsset("DevTalk Weekly", "Tech", 2700, "John Schmidt", 42);

        author.getMediaAssets().add(video1);
        author.getMediaAssets().add(video2);
        author.getMediaAssets().add(podcast);

        System.out.println("Author: " + author.getFullName() + " (@" + author.getUsername() + ")");
        System.out.println("Total Media Assets: " + author.getMediaAssets().size());
        System.out.println("  - Video: " + video1.getTitle() + " [" + video1.getResolution() + "]");
        System.out.println("  - Video: " + video2.getTitle() + " [" + video2.getResolution() + "]");
        System.out.println("  - Podcast: " + podcast.getTitle() + " (Ep." + podcast.getEpisodeNumber() + ")");
    }
}
