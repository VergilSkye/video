import { Component, OnInit, OnDestroy, HostListener, AfterViewInit } from '@angular/core';

import { OpenVidu, Publisher, Session, StreamEvent, StreamManager, Subscriber } from 'openvidu-browser';
import { ActivatedRoute } from '@angular/router';

import { Consulta } from 'app/shared/model/consulta.model';
import { VideoSessionService } from 'app/entities/consulta/video/video-session.service';
@Component({
  selector: 'jhi-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit, OnDestroy {
  consulta: Consulta;

  // OpenVidu objects
  OV: OpenVidu;
  session: Session;
  publisher: StreamManager; // Local
  subscribers: StreamManager[] = []; // Remotes

  token: string;

  // Main video of the page, will be 'publisher' or one of the 'subscribers',
  // updated by click event in UserVideoComponent children
  mainStreamManager: StreamManager;

  constructor(private videoSessionService: VideoSessionService, protected activatedRoute: ActivatedRoute) {}

  @HostListener('window:beforeunload')
  beforeunloadHandler() {
    // On window closed leave session
    this.leaveSession();
  }

  ngOnDestroy() {
    // On component destroyed leave session
    this.leaveSession();
  }

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ consulta }) => {
      this.consulta = consulta;
      this.joinSession();
    });
  }

  joinSession() {
    // --- 1) Get an OpenVidu object ---
    console.warn('1');
    this.OV = new OpenVidu();

    // --- 2) Init a session ---

    this.session = this.OV.initSession();
    console.warn('2');
    // --- 3) Specify the actions when events take place in the session ---

    // On every new Stream received...
    this.session.on('streamCreated', (event: StreamEvent) => {
      console.warn('STREAM CREATED!');
      console.warn(event.stream);
      // Subscribe to the Stream to receive it. Second parameter is undefined
      // so OpenVidu doesn't create an HTML video by its own
      let subscriber: Subscriber = this.session.subscribe(event.stream, undefined);
      this.subscribers.push(subscriber);
    });

    // On every Stream destroyed...
    this.session.on('streamDestroyed', (event: StreamEvent) => {
      // Remove the stream from 'subscribers' array
      this.deleteSubscriber(event.stream.streamManager);
      console.warn('STREAM DESTROYED!');
      console.warn(event.stream);
    });
    // --- 4) Connect to the session with a valid user token ---

    // 'getToken' method is simulating what your server-side should do.
    // 'token' parameter should be retrieved and returned by your own backend
    console.warn('3');
    this.videoSessionService.generateToken(this.consulta.id).subscribe(res => {
      console.warn('4');
      let token = res[0];
      console.warn('Token: ' + token);
      console.warn('5', this.consulta.psicologo_idLogin);
      this.session
        .connect(token, { clientData: 'PORRA' })
        .then(() => {
          console.warn('6');
          // --- 5) Get your own camera stream ---

          // Init a publisher passing undefined as targetElement (we don't want OpenVidu to insert a video
          // element: we will manage it on our own) and with the desired properties
          let publisher: Publisher = this.OV.initPublisher(undefined, {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: '640x480', // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
            mirror: false // Whether to mirror your local video or not
          });
          console.warn('7', publisher);
          // --- 6) Publish your stream ---

          this.session.publish(publisher);
          console.warn('8');
          // Set the main video in the page to display our webcam and store our Publisher
          this.mainStreamManager = publisher;
          this.publisher = publisher;
          console.warn('9');
        })
        .catch(error => {
          console.warn('10');
          console.log('There was an error connecting to the session:', error.code, error.message);
        });
    });
  }
  leaveSession() {
    // --- 7) Leave the session by calling 'disconnect' method over the Session object ---

    if (this.session) {
      this.session.disconnect();
    }

    // Empty all properties...
    this.subscribers = [];
    delete this.publisher;
    delete this.session;
    delete this.OV;
  }
  private deleteSubscriber(streamManager: StreamManager): void {
    let index = this.subscribers.indexOf(streamManager, 0);
    if (index > -1) {
      this.subscribers.splice(index, 1);
    }
  }

  updateMainStreamManager(streamManager: StreamManager) {
    this.mainStreamManager = streamManager;
  }
}
