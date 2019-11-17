import {Component, OnInit, OnDestroy, HostListener, AfterViewInit} from '@angular/core';
import { HttpHeaders, HttpResponse, HttpClient } from '@angular/common/http';
import {
  ConnectionEvent,
  OpenVidu,
  Publisher,
  PublisherProperties,
  Session,
  StreamEvent,
  StreamManager,
  Subscriber
} from 'openvidu-browser';
import { MatSnackBar } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, throwError as observableThrowError  } from 'rxjs';
import { catchError } from 'rxjs/operators'
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';

import {Consulta, IConsulta} from 'app/shared/model/consulta.model';
import {VideoSessionService} from "app/entities/consulta/video/video-session.service";



@Component({
  selector: 'jhi-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit, OnDestroy, AfterViewInit  {

  consulta: Consulta;


  OV: OpenVidu;
  session: Session;
  publisher: Publisher;

  token: string;

  cameraOptions: PublisherProperties;

  localVideoActivated: boolean;
  localAudioActivated: boolean;
  videoIcon: string;
  audioIcon: string;
  fullscreenIcon: string;

  constructor(
    public location: Location,
    private videoSessionService: VideoSessionService,
    private snackBar: MatSnackBar) { }

  OPEN_VIDU_CONNECTION() {

    // 0) Obtain 'token' from server
    // In this case, the method ngOnInit takes care of it


    // 1) Initialize OpenVidu and your Session
    this.OV = new OpenVidu();
    this.session = this.OV.initSession();

    // 2) Specify the actions when events take place
    this.session.on('streamCreated', (event: StreamEvent) => {
      console.warn('STREAM CREATED!');
      console.warn(event.stream);
      this.session.subscribe(event.stream, 'subscriber', {
        insertMode: 'APPEND'
      });
    });

    this.session.on('streamDestroyed', (event: StreamEvent) => {
      console.warn('STREAM DESTROYED!');
      console.warn(event.stream);
    });

    this.session.on('connectionCreated', (event: ConnectionEvent) => {
      if (event.connection.connectionId === this.session.connection.connectionId) {
        console.warn('YOUR OWN CONNECTION CREATED!');
      } else {
        console.warn('OTHER USER\'S CONNECTION CREATED!');
      }
      console.warn(event.connection);
    });

    this.session.on('connectionDestroyed', (event: ConnectionEvent) => {
      console.warn('OTHER USER\'S CONNECTION DESTROYED!');
      console.warn(event.connection);
      /* if (this.authenticationService.connectionBelongsToTeacher(event.connection)) {
        this.location.back();
      }*/
    });

    // 3) Connect to the session
    this.session.connect(this.token, 'CLIENT:' + 'aaaa').then(() => {

        // 4) Get your own camera stream with the desired resolution and publish it, only if the user is supposed to do so
        this.publisher = this.OV.initPublisher('publisher', this.cameraOptions);

        this.publisher.on('accessAllowed', () => {
          console.warn('CAMERA ACCESS ALLOWED!');
        });
        this.publisher.on('accessDenied', () => {
          console.warn('CAMERA ACCESS DENIED!');
        });
        this.publisher.on('streamCreated', (event: StreamEvent) => {
          console.warn('STREAM CREATED BY PUBLISHER!');
          console.warn(event.stream);
        })

        // 5) Publish your stream
        this.session.publish(this.publisher);

    }).catch(error => {
      console.log('There was an error connecting to the session:', error.code, error.message);
    });

}
